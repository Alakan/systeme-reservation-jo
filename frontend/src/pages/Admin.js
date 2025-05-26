// src/pages/Admin.js
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import '../styles/Admin.css';
import { useTheme } from '../contexts/ThemeContext';

function Admin() {
  const [activeTab, setActiveTab] = useState('utilisateurs'); // onglet par défaut "Utilisateurs"
  const [data, setData] = useState([]); // stocke les données chargées en fonction de l'onglet
  const { theme, setTheme } = useTheme();
  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  // Vérification d'accès administrateur
  useEffect(() => {
    if (!token) {
      navigate('/login');
      return;
    }
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      console.log('Token payload (Admin):', payload);

      const rolesFromToken = payload.roles;
      let isAdminFlag = false;
      if (rolesFromToken) {
        if (Array.isArray(rolesFromToken)) {
          isAdminFlag = rolesFromToken.some((role) => {
            const roleUpper = role.toString().toUpperCase();
            return (
              roleUpper === 'ADMIN' ||
              roleUpper === 'ROLE_ADMIN' ||
              roleUpper === 'ROLE_ADMINISTRATEUR'
            );
          });
        } else if (typeof rolesFromToken === 'string') {
          const roleUpper = rolesFromToken.toUpperCase();
          isAdminFlag =
            roleUpper === 'ADMIN' ||
            roleUpper === 'ROLE_ADMIN' ||
            roleUpper === 'ROLE_ADMINISTRATEUR';
        }
      }
      if (!isAdminFlag) {
        alert("Accès refusé : vous n'êtes pas administrateur.");
        navigate('/');
      }
    } catch (error) {
      console.error("Erreur lors du décodage du token :", error);
      navigate('/login');
    }
  }, [navigate, token]);

  // Chargement des données en fonction de l'onglet actif
  useEffect(() => {
    if (!token) return;

    let endpoint = '';
    switch (activeTab) {
      case 'utilisateurs':
        endpoint = '/admin/utilisateurs';
        break;
      case 'evenements':
        endpoint = '/admin/evenements';
        break;
      case 'reservations':
        endpoint = '/admin/reservations';
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
  }, [activeTab, token]);

  // Fonction pour basculer manuellement le thème (mode dark / clair)
  const toggleTheme = () => {
    setTheme(theme === 'dark' ? 'light' : 'dark');
  };

  return (
    <div className={`admin-container ${theme}`}>
      <h1>Tableau de bord Administrateur</h1>
      <button onClick={toggleTheme} className="toggle-theme">
        Bascule thème ({theme === 'dark' ? 'Clair' : 'Sombre'})
      </button>
      <nav className="admin-nav">
        <button
          className={activeTab === 'utilisateurs' ? 'active' : ''}
          onClick={() => setActiveTab('utilisateurs')}
        >
          Utilisateurs
        </button>
        <button
          className={activeTab === 'evenements' ? 'active' : ''}
          onClick={() => setActiveTab('evenements')}
        >
          Évènements
        </button>
        <button
          className={activeTab === 'reservations' ? 'active' : ''}
          onClick={() => setActiveTab('reservations')}
        >
          Réservations
        </button>
      </nav>
      <div className="admin-content">
        {/* Gestion des Utilisateurs */}
        {activeTab === 'utilisateurs' && (
          <div>
            <h2>Gestion des Utilisateurs</h2>
            <button onClick={() => navigate('/admin/ajouter-utilisateur')}>
              Ajouter un utilisateur / administrateur
            </button>
            {data && Array.isArray(data) && data.length > 0 ? (
              <ul>
                {data.map((user) => (
                  <li key={user.id}>
                    {user.username} - {user.email}{" "}
                    <button onClick={() => navigate(`/admin/modifier-utilisateur/${user.id}`)}>
                      Modifier le profil
                    </button>
                    <button
                      onClick={() => {
                        if (window.confirm("Voulez-vous vraiment supprimer cet utilisateur ?")) {
                          api
                            .delete(`/admin/supprimer-utilisateur/${user.id}`, {
                              headers: { Authorization: `Bearer ${token}` },
                            })
                            .then(() => {
                              setData(data.filter((u) => u.id !== user.id));
                            })
                            .catch((error) => {
                              console.error("Erreur lors de la suppression de l'utilisateur :", error);
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
        {activeTab === 'evenements' && (
          <div>
            <h2>Gestion des Évènements</h2>
            <button onClick={() => navigate('/admin/ajouter-evenement')}>
              Ajouter un événement
            </button>
            {data && Array.isArray(data) && data.length > 0 ? (
              <ul>
                {data.map((event) => (
                  <li key={event.id}>
                    {event.titre} - {event.dateEvenement}{" "}
                    <button onClick={() => navigate(`/admin/modifier-evenement/${event.id}`)}>
                      Modifier
                    </button>
                    <button
                      onClick={() => {
                        if (window.confirm("Voulez-vous vraiment supprimer cet événement ?")) {
                          api
                            .delete(`/admin/supprimer-evenement/${event.id}`, {
                              headers: { Authorization: `Bearer ${token}` },
                            })
                            .then(() => {
                              setData(data.filter((e) => e.id !== event.id));
                            })
                            .catch((error) => {
                              console.error("Erreur lors de la suppression de l'événement :", error);
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
              <p>Aucun événement trouvé.</p>
            )}
          </div>
        )}

        {/* Gestion des Réservations */}
        {activeTab === 'reservations' && (
          <div>
            <h2>Gestion des Réservations</h2>
            <button onClick={() => navigate('/admin/ajouter-reservation')}>
              Ajouter une réservation
            </button>
            {data && Array.isArray(data) && data.length > 0 ? (
              <ul>
                {data.map((res) => {
                  const reservationId = res.id ? `Réservation #${res.id}` : "Réservation inconnue";
                  const eventTitle =
                    res.evenement && res.evenement.titre
                      ? res.evenement.titre
                      : "Aucun événement associé";
                  const statut = res.statut ? res.statut : "Non défini";
                  return (
                    <li key={res.id || Math.random()}>
                      {reservationId} pour l'événement {eventTitle} - Statut : {statut}{" "}
                      <button onClick={() => navigate(`/admin/modifier-reservation/${res.id}`)}>
                        Modifier
                      </button>
                      <button
                        onClick={() => {
                          if (
                            window.confirm("Voulez-vous vraiment supprimer cette réservation ?")
                          ) {
                            api
                              .delete(`/admin/supprimer-reservation/${res.id}`, {
                                headers: { Authorization: `Bearer ${token}` },
                              })
                              .then(() => {
                                setData(data.filter((r) => r.id !== res.id));
                              })
                              .catch((error) => {
                                console.error(
                                  "Erreur lors de la suppression de la réservation :",
                                  error
                                );
                              });
                          }
                        }}
                      >
                        Supprimer
                      </button>
                    </li>
                  );
                })}
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
