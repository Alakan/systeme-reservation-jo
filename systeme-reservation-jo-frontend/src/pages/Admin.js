// src/pages/Admin.js
import React, { useState, useEffect } from "react";
import { useNavigate }                from "react-router-dom";
import api                             from "../services/api";
import "../styles/Admin.css";
import { useTheme }                    from "../contexts/ThemeContext";

export default function Admin() {
  const [activeTab, setActiveTab] = useState("utilisateurs");
  const [data, setData]           = useState([]);
  const { theme, setTheme }       = useTheme();
  const navigate                  = useNavigate();
  const token                     = localStorage.getItem("token");

  // Vérification du rôle ADMIN
  useEffect(() => {
    if (!token) return navigate("/login");
    try {
      const payload  = JSON.parse(atob(token.split(".")[1]));
      const roles    = Array.isArray(payload.roles)
        ? payload.roles
        : [payload.roles];
      const isAdmin  = roles
        .map(r => r.replace(/^ROLE_/, "").toUpperCase())
        .includes("ADMINISTRATEUR");
      if (!isAdmin) {
        alert("Accès refusé : vous n'êtes pas administrateur.");
        navigate("/");
      }
    } catch {
      navigate("/login");
    }
  }, [navigate, token]);

  // Chargement des données
  const fetchData = () => {
    if (!token) return;
    const headers = { Authorization: `Bearer ${token}` };

    // Double-fetch pour enrichir les réservations
    if (activeTab === "reservations") {
      Promise.all([
        api.get("admin/reservations", { headers }),
        api.get("admin/evenements",   { headers })
      ])
      .then(([resRes, evRes]) => {
        // Map id événement → événement complet
        const eventsMap = new Map(evRes.data.map(ev => [ev.id, ev]));
        // Enrichir chaque réservation
        const enriched = resRes.data.map(r => ({
          ...r,
          // adapter selon le champ retourné par ton API
          evenement: eventsMap.get(r.evenementId) ?? { titre: "N/A" }
        }));
        setData(enriched);
      })
      .catch(err =>
        console.error("API reservations+events error:", err)
      );
      return;
    }

    // Fetch standard pour utilisateurs & événements
    const endpoint = {
      utilisateurs: "admin/utilisateurs",
      evenements:   "admin/evenements"
    }[activeTab];
    if (!endpoint) return;

    api.get(endpoint, { headers })
      .then(res => setData(res.data))
      .catch(err => console.error("API error:", err));
  };
  useEffect(fetchData, [activeTab, token]);

  // Désactiver / réactiver
  const toggleItem = (type, id, action) => {
    if (
      window.confirm(
        `${action === "desactiver" ? "Désactiver" : "Réactiver"} cet ${type} ?`
      )
    ) {
      api.put(`admin/${type}/${id}/${action}`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      }).then(fetchData);
    }
  };

  // Thème
  const toggleTheme = () =>
    setTheme(theme === "dark" ? "light" : "dark");

  return (
    <div className="admin-container" data-theme={theme}>
      <h1>Tableau de bord Administrateur</h1>
      <button onClick={toggleTheme} className="toggle-theme">
        Thème : {theme === "dark" ? "Clair" : "Sombre"}
      </button>

      {/* Navigation onglets */}
      <nav className="admin-nav">
        {["utilisateurs", "evenements", "reservations"].map(tab => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={activeTab === tab ? "active" : ""}
          >
            {tab.charAt(0).toUpperCase() + tab.slice(1)}
          </button>
        ))}
      </nav>

      <div className="admin-content">
        {activeTab === "utilisateurs" && (
          <section>
            <div className="section-header">
              <h2>Utilisateurs</h2>
              <button
                className="btn-add"
                onClick={() => navigate("/admin/utilisateurs/ajouter")}
              >
                + Ajouter
              </button>
            </div>
            {data.length > 0 ? (
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>Utilisateur</th>
                    <th>Email</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {data.map(u => (
                    <tr key={u.id}>
                      <td>{u.username}</td>
                      <td>{u.email}</td>
                      <td className="actions">
                        <button
                          onClick={() =>
                            navigate(`/admin/utilisateurs/modifier/${u.id}`)
                          }
                        >
                          Modifier
                        </button>
                        <button
                          onClick={() =>
                            window
                              .confirm("Supprimer cet utilisateur ?") &&
                            api
                              .delete(`admin/utilisateurs/${u.id}`, {
                                headers: { Authorization: `Bearer ${token}` }
                              })
                              .then(fetchData)
                          }
                        >
                          Supprimer
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <p>Aucun utilisateur trouvé.</p>
            )}
          </section>
        )}

        {activeTab === "evenements" && (
          <section>
            <div className="section-header">
              <h2>Évènements</h2>
              <button
                className="btn-add"
                onClick={() => navigate("/admin/evenements/ajouter")}
              >
                + Ajouter
              </button>
            </div>
            {data.length > 0 ? (
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>Titre</th>
                    <th>Date</th>
                    <th>Statut</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {data.map(ev => (
                    <tr key={ev.id}>
                      <td>{ev.titre}</td>
                      <td>
                        {new Date(ev.dateEvenement).toLocaleString()}
                      </td>
                      <td>
                        <span
                          className={`status ${
                            ev.actif ? "active" : "disabled"
                          }`}
                        >
                          {ev.actif ? "Actif" : "Désactivé"}
                        </span>
                      </td>
                      <td className="actions">
                        <button
                          onClick={() =>
                            navigate(
                              `/admin/evenements/modifier/${ev.id}`
                            )
                          }
                        >
                          Modifier
                        </button>
                        {ev.actif ? (
                          <button
                            onClick={() =>
                              toggleItem("evenements", ev.id, "desactiver")
                            }
                          >
                            Désactiver
                          </button>
                        ) : (
                          <button
                            onClick={() =>
                              toggleItem("evenements", ev.id, "reactiver")
                            }
                          >
                            Réactiver
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <p>Aucun événement trouvé.</p>
            )}
          </section>
        )}

        {activeTab === "reservations" && (
          <section>
            <div className="section-header">
              <h2>Réservations</h2>
            </div>
            {data.length > 0 ? (
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Événement</th>
                    <th>Statut</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {data.map(r => (
                    <tr key={r.id}>
                      <td>#{r.id}</td>
                      <td>{r.evenement?.titre}</td>
                      <td>
                        <span
                          className={`status ${
                            r.actif ? "active" : "disabled"
                          }`}
                        >
                          {r.actif ? "Actif" : "Désactivé"}
                        </span>
                      </td>
                      <td className="actions">
                        <button
                          onClick={() =>
                            navigate(
                              `/admin/reservations/modifier/${r.id}`
                            )
                          }
                        >
                          Modifier
                        </button>
                        {r.actif ? (
                          <button
                            onClick={() =>
                              toggleItem(
                                "reservations",
                                r.id,
                                "desactiver"
                              )
                            }
                          >
                            Désactiver
                          </button>
                        ) : (
                          <button
                            onClick={() =>
                              toggleItem(
                                "reservations",
                                r.id,
                                "reactiver"
                              )
                            }
                          >
                            Réactiver
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <p>Aucune réservation trouvée.</p>
            )}
          </section>
        )}
      </div>
    </div>
  );
}
