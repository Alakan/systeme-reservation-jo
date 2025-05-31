// src/pages/Admin.js
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/Admin.css";
import { useTheme } from "../contexts/ThemeContext";

function Admin() {
  const [activeTab, setActiveTab] = useState("utilisateurs"); // Onglet par défaut
  const [data, setData] = useState([]); // Données chargées via l'API
  const { theme, setTheme } = useTheme();
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  // Vérification d'accès administrateur
  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }
    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      console.log("Token payload (Admin):", payload);
      const rolesFromToken = payload.roles;
      let isAdminFlag = false;
      if (rolesFromToken) {
        if (Array.isArray(rolesFromToken)) {
          isAdminFlag = rolesFromToken.some((role) => {
            const roleUpper = role.toString().toUpperCase();
            return (
              roleUpper === "ADMIN" ||
              roleUpper === "ROLE_ADMIN" ||
              roleUpper === "ROLE_ADMINISTRATEUR"
            );
          });
        } else if (typeof rolesFromToken === "string") {
          const roleUpper = rolesFromToken.toUpperCase();
          isAdminFlag =
            roleUpper === "ADMIN" ||
            roleUpper === "ROLE_ADMIN" ||
            roleUpper === "ROLE_ADMINISTRATEUR";
        }
      }
      if (!isAdminFlag) {
        alert("Accès refusé : vous n'êtes pas administrateur.");
        navigate("/");
      }
    } catch (error) {
      console.error("Erreur lors du décodage du token :", error);
      navigate("/login");
    }
  }, [navigate, token]);

  // Fonction de rafraîchissement des données en fonction de l'onglet actif
  const fetchData = () => {
    if (!token) return;
    let endpoint = "";
    switch (activeTab) {
      case "utilisateurs":
        endpoint = "/admin/utilisateurs";
        break;
      case "evenements":
        endpoint = "/admin/evenements";
        break;
      case "reservations":
        endpoint = "/admin/reservations";
        break;
      default:
        break;
    }
    if (endpoint) {
      api
        .get(endpoint, { headers: { Authorization: `Bearer ${token}` } })
        .then((res) => {
          setData(res.data);
        })
        .catch((error) => {
          console.error("Erreur lors du chargement des données:", error);
        });
    }
  };

  // Recharger les données à chaque changement d'onglet
  useEffect(() => {
    fetchData();
  }, [activeTab, token]);

  // Fonction pour basculer le thème (mode sombre / clair)
  const toggleTheme = () => {
    setTheme(theme === "dark" ? "light" : "dark");
  };

  // Fonctions de désactivation et de réactivation pour les événements
  const handleDesactiverEvenement = (eventId) => {
    if (
      window.confirm("Voulez-vous vraiment désactiver cet événement ?")
    ) {
      api
        .put(
          `/admin/evenements/${eventId}/desactiver`,
          {},
          { headers: { Authorization: `Bearer ${token}` } }
        )
        .then(() => fetchData())
        .catch((error) => {
          console.error(
            "Erreur lors de la désactivation de l'événement :",
            error
          );
        });
    }
  };

  const handleReactiverEvenement = (eventId) => {
    if (window.confirm("Voulez-vous réactiver cet événement ?")) {
      api
        .put(
          `/admin/evenements/${eventId}/reactiver`,
          {},
          { headers: { Authorization: `Bearer ${token}` } }
        )
        .then(() => fetchData())
        .catch((error) => {
          console.error(
            "Erreur lors de la réactivation de l'événement :",
            error
          );
        });
    }
  };

  // Fonctions de désactivation et de réactivation pour les réservations
  const handleDesactiverReservation = (reservationId) => {
    if (
      window.confirm(
        "Voulez-vous désactiver cette réservation ?"
      )
    ) {
      api
        .put(
          `/admin/reservations/${reservationId}/desactiver`,
          {},
          { headers: { Authorization: `Bearer ${token}` } }
        )
        .then(() => fetchData())
        .catch((error) => {
          console.error(
            "Erreur lors de la désactivation de la réservation :",
            error
          );
        });
    }
  };

  const handleReactiverReservation = (reservationId) => {
    if (
      window.confirm(
        "Voulez-vous réactiver cette réservation ?"
      )
    ) {
      api
        .put(
          `/admin/reservations/${reservationId}/reactiver`,
          {},
          { headers: { Authorization: `Bearer ${token}` } }
        )
        .then(() => fetchData())
        .catch((error) => {
          console.error(
            "Erreur lors de la réactivation de la réservation :",
            error
          );
        });
    }
  };

  return (
    <div className={`admin-container ${theme}`}>
      <h1>Tableau de bord Administrateur</h1>
      <button onClick={toggleTheme} className="toggle-theme">
        Bascule thème ({theme === "dark" ? "Clair" : "Sombre"})
      </button>
      <nav className="admin-nav">
        <button
          className={activeTab === "utilisateurs" ? "active" : ""}
          onClick={() => setActiveTab("utilisateurs")}
        >
          Utilisateurs
        </button>
        <button
          className={activeTab === "evenements" ? "active" : ""}
          onClick={() => setActiveTab("evenements")}
        >
          Évènements
        </button>
        <button
          className={activeTab === "reservations" ? "active" : ""}
          onClick={() => setActiveTab("reservations")}
        >
          Réservations
        </button>
      </nav>
      <div className="admin-content">
        {/* Gestion des Utilisateurs */}
        {activeTab === "utilisateurs" && (
          <div>
            <h2>Gestion des Utilisateurs</h2>
            <button onClick={() => navigate("/admin/ajouter-utilisateur")}>
              Ajouter un utilisateur / administrateur
            </button>
            {data && Array.isArray(data) && data.length > 0 ? (
              <ul>
                {data.map((user) => (
                  <li key={user.id}>
                    {user.username} - {user.email}{" "}
                    <button
                      onClick={() =>
                        navigate(`/admin/modifier-utilisateur/${user.id}`)
                      }
                    >
                      Modifier le profil
                    </button>
                    <button
                      onClick={() => {
                        if (
                          window.confirm(
                            "Voulez-vous vraiment supprimer cet utilisateur ?"
                          )
                        ) {
                          api
                            .delete(`/admin/utilisateurs/${user.id}`, {
                              headers: {
                                Authorization: `Bearer ${token}`,
                              },
                            })
                            .then(() => {
                              fetchData(); // Rafraîchit les données après suppression
                            })
                            .catch((error) => {
                              console.error(
                                "Erreur lors de la suppression de l'utilisateur :",
                                error
                              );
                            });
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
            {data && Array.isArray(data) && data.length > 0 ? (
              <ul>
                {data.map((event) => (
                  <li key={event.id}>
                    {event.titre} -{" "}
                    {new Date(event.dateEvenement).toLocaleString()}{" "}
                    {event.actif ? (
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
                        navigate(`/admin/modifier-evenement/${event.id}`)
                      }
                    >
                      Modifier
                    </button>
                    {event.actif ? (
                      <button
                        onClick={() =>
                          handleDesactiverEvenement(event.id)
                        }
                      >
                        Désactiver
                      </button>
                    ) : (
                      <button
                        onClick={() =>
                          handleReactiverEvenement(event.id)
                        }
                      >
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
            <button onClick={() => navigate("/admin/ajouter-reservation")}>
              Ajouter une réservation
            </button>
            {data && Array.isArray(data) && data.length > 0 ? (
              <ul>
                {data.map((res) => (
                  <li key={res.id}>
                    Réservation #{res.id} pour{" "}
                    {res.evenement?.titre || "Événement inconnu"} - Statut :{" "}
                    {res.statut}{" "}
                    {res.actif ? (
                      <span
                        style={{ color: "green", fontWeight: "bold" }}
                      >
                        [Actif]
                      </span>
                    ) : (
                      <span
                        style={{ color: "red", fontWeight: "bold" }}
                      >
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
                        onClick={() =>
                          handleDesactiverReservation(res.id)
                        }
                      >
                        Désactiver
                      </button>
                    ) : (
                      <button
                        onClick={() =>
                          handleReactiverReservation(res.id)
                        }
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
