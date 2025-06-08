// src/pages/DashboardUtilisateurs.js
import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';
import '../styles/DashboardUtilisateurs.css';

function DashboardUtilisateurs() {
  const [profile, setProfile] = useState(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('Vous devez être connecté pour accéder à cet espace.');
      navigate('/login');
      return;
    }

    // Décodage du token pour récupérer les rôles
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      console.log('Token payload:', payload);
      const rolesFromToken = payload.roles;
      let adminFlag = false;
      if (rolesFromToken) {
        if (Array.isArray(rolesFromToken)) {
          adminFlag = rolesFromToken.some((role) => {
            const roleUpper = role.toString().toUpperCase();
            return (
              roleUpper === 'ADMIN' ||
              roleUpper === 'ROLE_ADMIN' ||
              roleUpper === 'ROLE_ADMINISTRATEUR'
            );
          });
        } else if (typeof rolesFromToken === 'string') {
          const roleUpper = rolesFromToken.toUpperCase();
          adminFlag =
            roleUpper === 'ADMIN' ||
            roleUpper === 'ROLE_ADMIN' ||
            roleUpper === 'ROLE_ADMINISTRATEUR';
        }
      }
      setIsAdmin(adminFlag);
      console.log('isAdmin ?', adminFlag);
    } catch (error) {
      console.error('Erreur lors du décodage du token:', error);
      setIsAdmin(false);
    }

    // Appel sécurisé pour récupérer le profil complet de l'utilisateur
    // Modification : utilisation d'un chemin relatif sans slash initial ("utilisateurs/me")
    api
      .get('utilisateurs/me', {
        headers: { Authorization: `Bearer ${token}` }
      })
      .then((response) => {
        setProfile(response.data);
        console.log('Profil récupéré :', response.data);
      })
      .catch((error) => {
        console.error('Erreur lors de la récupération du profil :', error);
        navigate('/login');
      });
  }, [navigate]);

  const title = isAdmin ? "Espace Administrateur" : "Espace Utilisateur";

  return (
    <div className="dashboard-container">
      <h1>{title}</h1>
      {profile ? (
        <>
          <div className="profile-info">
            <p>
              <strong>Nom :</strong> {profile.username || 'Non renseigné'}
            </p>
            <p>
              <strong>Email :</strong> {profile.email || 'Non renseigné'}
            </p>
          </div>
          <div className="dashboard-links">
            {isAdmin ? (
              <>
                <Link to="/admin">
                  <button>Accéder à l'administration</button>
                </Link>
                {/* Vous pouvez ajouter d'autres boutons spécifiques aux administrateurs ici */}
              </>
            ) : (
              <>
                <Link to="/mes-reservations">
                  <button>Mes Réservations</button>
                </Link>
                <Link to="/modifier-profil">
                  <button>Modifier mon profil</button>
                </Link>
              </>
            )}
          </div>
        </>
      ) : (
        <p>Chargement des informations...</p>
      )}
    </div>
  );
}

export default DashboardUtilisateurs;
