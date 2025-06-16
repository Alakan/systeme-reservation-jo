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
  const token                      = localStorage.getItem("token");

  // Vérification du rôle ADMIN
  useEffect(() => {
    if (!token) return navigate("/login");
    try {
      const payload  = JSON.parse(atob(token.split(".")[1]));
      const rawRoles = Array.isArray(payload.roles)
        ? payload.roles
        : [payload.roles];
      const isAdmin = rawRoles
        .map(r => r.toString().toUpperCase().replace(/^ROLE_/, ""))
        .includes("ADMINISTRATEUR");
      if (!isAdmin) {
        alert("Accès refusé : vous n'êtes pas administrateur.");
        navigate("/");
      }
    } catch {
      navigate("/login");
    }
  }, [navigate, token]);

  // Chargement des données selon l'onglet
  const fetchData = () => {
    if (!token) return;
    let endpoint = "";
    if (activeTab === "utilisateurs")   endpoint = "admin/utilisateurs";
    if (activeTab === "evenements")     endpoint = "admin/evenements";
    if (activeTab === "reservations")   endpoint = "admin/reservations";

    if (!endpoint) return;
    api.get(endpoint, { headers: { Authorization: `Bearer ${token}` } })
      .then(res => setData(res.data))
      .catch(err => console.error("API error:", err));
  };
  useEffect(fetchData, [activeTab, token]);

  // Handlers Événements
  const handleDesactiverEvenement = id => {
    if (window.confirm("Désactiver cet événement ?")) {
      api.put(`admin/evenements/${id}/desactiver`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      }).then(fetchData);
    }
  };
  const handleReactiverEvenement = id => {
    if (window.confirm("Réactiver cet événement ?")) {
      api.put(`admin/evenements/${id}/reactiver`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      }).then(fetchData);
    }
  };

  // Handlers Réservations
  const handleDesactiverReservation = id => {
    if (window.confirm("Désactiver cette réservation ?")) {
      api.put(`admin/reservations/${id}/desactiver`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      }).then(fetchData);
    }
  };
  const handleReactiverReservation = id => {
    if (window.confirm("Réactiver cette réservation ?")) {
      api.put(`admin/reservations/${id}/reactiver`, {}, {
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
        Bascule thème ({theme === "dark" ? "Clair" : "Sombre"})
      </button>

      <nav className="admin-nav">
        {["utilisateurs", "evenements", "reservations"].map(tab => (
          <button
            key={tab}
            className={activeTab === tab ? "active" : ""}
            onClick={() => setActiveTab(tab)}
          >
            {tab.charAt(0).toUpperCase() + tab.slice(1)}
          </button>
        ))}
      </nav>

      <div className="admin-content">
        {activeTab === "utilisateurs" && (
          <section>
            <h2>Gestion des Utilisateurs</h2>
            <button
              className="btn-add"
              onClick={() => navigate("/admin/utilisateurs/ajouter")}
            >
              + Ajouter un utilisateur
            </button>
            {data.length > 0 ? (
              <ul className="admin-list">
                {data.map(u => (
                  <li key={u.id}>
                    {u.username} ({u.email})
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
                        api.delete(`admin/utilisateurs/${u.id}`, {
                          headers: { Authorization: `Bearer ${token}` }
                        }).then(fetchData)
                      }
                    >
                      Supprimer
                    </button>
                  </li>
                ))}
              </ul>
            ) : (
              <p>Aucun utilisateur trouvé.</p>
            )}
          </section>
        )}

        {activeTab === "evenements" && (
          <section>
            <h2>Gestion des Évènements</h2>
            <button
              className="btn-add"
              onClick={() => navigate("/admin/evenements/ajouter")}
            >
              + Ajouter un événement
            </button>
            {data.length > 0 ? (
              <ul className="admin-list">
                {data.map(ev => (
                  <li key={ev.id}>
                    {ev.titre} — {new Date(ev.dateEvenement).toLocaleString()}
                    <span className={`status ${ev.actif ? "active" : "disabled"}`}>
                      [{ev.actif ? "Actif" : "Désactivé"}]
                    </span>
                    <button
                      onClick={() =>
                        navigate(`/admin/evenements/modifier/${ev.id}`)
                      }
                    >
                      Modifier
                    </button>
                    {ev.actif ? (
                      <button onClick={() => handleDesactiverEvenement(ev.id)}>
                        Désactiver
                      </button>
                    ) : (
                      <button onClick={() => handleReactiverEvenement(ev.id)}>
                        Réactiver
                      </button>
                    )}
                  </li>
                ))}
              </ul>
            ) : (
              <p>Aucun événement trouvé.</p>
            )}
          </section>
        )}

        {activeTab === "reservations" && (
          <section>
            <h2>Gestion des Réservations</h2>
            {data.length > 0 ? (
              <ul className="admin-list">
                {data.map(r => (
                  <li key={r.id}>
                    Réservation #{r.id} pour {r.evenement?.titre || "N/A"}
                    <span className={`status ${r.actif ? "active" : "disabled"}`}>
                      [{r.actif ? "Actif" : "Désactivé"}]
                    </span>
                    <button
                      onClick={() =>
                        navigate(`/admin/reservations/modifier/${r.id}`)
                      }
                    >
                      Modifier
                    </button>
                    {r.actif ? (
                      <button onClick={() => handleDesactiverReservation(r.id)}>
                        Désactiver
                      </button>
                    ) : (
                      <button onClick={() => handleReactiverReservation(r.id)}>
                        Réactiver
                      </button>
                    )}
                  </li>
                ))}
              </ul>
            ) : (
              <p>Aucune réservation trouvée.</p>
            )}
          </section>
        )}
      </div>
    </div>
  );
}
