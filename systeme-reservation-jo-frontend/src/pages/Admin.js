// src/pages/Admin.js
import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/Admin.css";
import { useTheme } from "../contexts/ThemeContext";

function Admin() {
  const [activeTab, setActiveTab] = useState("utilisateurs");
  const [data, setData] = useState([]);
  const { theme, setTheme } = useTheme();
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  // 1) Vérifier que l'utilisateur est admin
  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }
    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      const roles = payload.roles;
      const isAdmin =
        Array.isArray(roles)
          ? roles.some((r) =>
              ["ADMIN", "ROLE_ADMIN", "ROLE_ADMINISTRATEUR"].includes(r.toString().toUpperCase())
            )
          : ["ADMIN", "ROLE_ADMIN", "ROLE_ADMINISTRATEUR"].includes((roles || "").toString().toUpperCase());

      if (!isAdmin) {
        alert("Accès refusé : vous n'êtes pas administrateur.");
        navigate("/");
      }
    } catch (err) {
      console.error("Décodage token admin échoué :", err);
      navigate("/login");
    }
  }, [navigate, token]);

  // 2) fetchData encapsulé pour gérer les deps ESLint
  const fetchData = useCallback(async () => {
    if (!token) return;

    let endpoint = "";
    if (activeTab === "utilisateurs") endpoint = "admin/utilisateurs";
    else if (activeTab === "evenements") endpoint = "admin/evenements";
    else if (activeTab === "reservations") endpoint = "admin/reservations";

    if (!endpoint) return;

    try {
      const res = await api.get(endpoint, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setData(res.data);
    } catch (err) {
      console.error("Erreur chargement données admin :", err);
    }
  }, [activeTab, token]);

  // 3) Lancer fetchData quand activeTab (via fetchData) change
  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // 4) Handlers pour activer/désactiver entités
  const handleDesactiverEvenement = async (id) => {
    if (!window.confirm("Désactiver cet événement ?")) return;
    try {
      await api.put(`admin/evenements/${id}/desactiver`, null, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchData();
    } catch (err) {
      console.error("Désactivation événement échouée :", err);
    }
  };

  const handleReactiverEvenement = async (id) => {
    if (!window.confirm("Réactiver cet événement ?")) return;
    try {
      await api.put(`admin/evenements/${id}/reactiver`, null, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchData();
    } catch (err) {
      console.error("Réactivation événement échouée :", err);
    }
  };

  const handleDesactiverReservation = async (id) => {
    if (!window.confirm("Désactiver cette réservation ?")) return;
    try {
      await api.put(`admin/reservations/${id}/desactiver`, null, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchData();
    } catch (err) {
      console.error("Désactivation réservation échouée :", err);
    }
  };

  const handleReactiverReservation = async (id) => {
    if (!window.confirm("Réactiver cette réservation ?")) return;
    try {
      await api.put(`admin/reservations/${id}/reactiver`, null, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchData();
    } catch (err) {
      console.error("Réactivation réservation échouée :", err);
    }
  };

  // 5) Gestion du thème
  const toggleTheme = () => setTheme(theme === "dark" ? "light" : "dark");

  return (
    <div className={`admin-container ${theme}`}>
      <h1>Tableau de bord Administrateur</h1>
      <button onClick={toggleTheme} className="toggle-theme">
        Bascule thème ({theme === "dark" ? "Clair" : "Sombre"})
      </button>

      <nav className="admin-nav">
        {["utilisateurs", "evenements", "reservations"].map((tab) => (
          <button
            key={tab}
            className={activeTab === tab ? "active" : ""}
            onClick={() => setActiveTab(tab)}
          >
            {tab === "utilisateurs"
              ? "Utilisateurs"
              : tab === "evenements"
              ? "Évènements"
              : "Réservations"}
          </button>
        ))}
      </nav>

      <div className="admin-content">
        {/* Gestion des Utilisateurs */}
        {activeTab === "utilisateurs" && (
          <div>
            <h2>Gestion des Utilisateurs</h2>
            <button onClick={() => navigate("/admin/ajouter-utilisateur")}>
              Ajouter un utilisateur
            </button>
            {Array.isArray(data) && data.length > 0 ? (
              <ul>
                {data.map((u) => (
                  <li key={u.id}>
                    {u.username} – {u.email}
                    <button
                      onClick={() =>
                        navigate(`/admin/modifier-utilisateur/${u.id}`)
                      }
                    >
                      Modifier
                    </button>
                    <button
                      onClick={async () => {
                        if (!window.confirm("Supprimer cet utilisateur ?"))
                          return;
                        try {
                          await api.delete(`admin/utilisateurs/${u.id}`, {
                            headers: { Authorization: `Bearer ${token}` },
                          });
                          fetchData();
                        } catch (err) {
                          console.error("Suppression utilisateur échouée :", err);
                        }
                      }}
                    >
                      Supprimer
                    </button>
                  </li>
                ))}
              </ul>
            ) : (
              <p>Aucun utilisateur trouvé.</p>
            )}
          </div>
        )}

        {/* Gestion des Évènements */}
        {activeTab === "evenements" && (
          <div>
            <h2>Gestion des Évènements</h2>
            <button onClick={() => navigate("/admin/ajouter-evenement")}>
              Ajouter un événement
            </button>
            {Array.isArray(data) && data.length > 0 ? (
              <ul>
                {data.map((e) => (
                  <li key={e.id}>
                    {e.titre} – {new Date(e.dateEvenement).toLocaleString()}{" "}
                    {e.actif ? (
                      <span style={{ color: "green", fontWeight: "bold" }}>
                        [Actif]
                      </span>
                    ) : (
                      <span style={{ color: "red", fontWeight: "bold" }}>
                        [Désactivé]
                      </span>
                    )}
                    <button
                      onClick={() =>
                        navigate(`/admin/modifier-evenement/${e.id}`)
                      }
                    >
                      Modifier
                    </button>
                    {e.actif ? (
                      <button onClick={() => handleDesactiverEvenement(e.id)}>
                        Désactiver
                      </button>
                    ) : (
                      <button onClick={() => handleReactiverEvenement(e.id)}>
                        Réactiver
                      </button>
                    )}
                  </li>
                ))}
              </ul>
            ) : (
              <p>Aucun événement trouvé.</p>
            )}
          </div>
        )}

        {/* Gestion des Réservations */}
        {activeTab === "reservations" && (
          <div>
            <h2>Gestion des Réservations</h2>
            {Array.isArray(data) && data.length > 0 ? (
              <ul>
                {data.map((res) => (
                  <li key={res.id}>
                    Réservation #{res.id} pour{" "}
                    {res.evenement?.titre || "Événement inconnu"} – Statut :{" "}
                    {res.statut}{" "}
                    {res.actif ? (
                      <span style={{ color: "green", fontWeight: "bold" }}>
                        [Actif]
                      </span>
                    ) : (
                      <span style={{ color: "red", fontWeight: "bold" }}>
                        [Désactivé]
                      </span>
                    )}
                    <button
                      onClick={() =>
                        navigate(`/admin/modifier-reservation/${res.id}`)
                      }
                    >
                      Modifier
                    </button>
                    {res.actif ? (
                      <button
                        onClick={() => handleDesactiverReservation(res.id)}
                      >
                        Désactiver
                      </button>
                    ) : (
                      <button
                        onClick={() => handleReactiverReservation(res.id)}
                      >
                        Réactiver
                      </button>
                    )}
                  </li>
                ))}
              </ul>
            ) : (
              <p>Aucune réservation trouvée.</p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default Admin;
