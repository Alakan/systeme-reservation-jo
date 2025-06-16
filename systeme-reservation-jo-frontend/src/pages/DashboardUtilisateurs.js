import React, { useEffect, useState } from 'react';
import { Link, useNavigate }          from 'react-router-dom';
import api                             from '../services/api';
import '../styles/DashboardUtilisateurs.css';

function DashboardUtilisateurs() {
  const [profile, setProfile] = useState(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const navigate              = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('Vous devez être connecté pour accéder à cet espace.');
      navigate('/login');
      return;
    }

    // décodage + normalisation des rôles
    let payload = {};
    try {
      payload = JSON.parse(atob(token.split('.')[1]));
      const rawRoles = Array.isArray(payload.roles)
        ? payload.roles
        : [payload.roles];
      const norm = rawRoles.map(r =>
        r.toString().toUpperCase().replace(/^ROLE_/, '')
      );
      setIsAdmin(norm.includes('ADMINISTRATEUR'));
    } catch {
      setIsAdmin(false);
    }

    // récupération du profil
    api.get('utilisateurs/me', {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(res => setProfile(res.data))
    .catch(() => navigate('/login'));
  }, [navigate]);

  // extrait le pseudo (avant @) ou renvoie la chaîne telle quelle
  const getDisplayName = login => {
    if (!login) return '';
    const idx = login.indexOf('@');
    return idx > 0 ? login.slice(0, idx) : login;
  };

  if (!profile) {
    return <p>Chargement des informations...</p>;
  }

  const displayName = getDisplayName(profile.username || profile.email);

  return (
    <div className="dashboard-container">
      <h1>{isAdmin ? 'Espace Administrateur' : 'Espace Utilisateur'}</h1>

      <div className="profile-info">
        <p>
          <strong>Nom :</strong> {displayName || 'Non renseigné'}
        </p>
        <p>
          <strong>Email :</strong> {profile.email || 'Non renseigné'}
        </p>
      </div>

      <div className="dashboard-links">
        {isAdmin ? (
          <Link to="/admin">
            <button>Accéder à l'administration</button>
          </Link>
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
    </div>
  );
}

export default DashboardUtilisateurs;
