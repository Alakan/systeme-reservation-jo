// src/pages/DashboardUtilisateurs.js
import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../styles/DashboardUtilisateurs.css';

function DashboardUtilisateurs() {
  const [profile, setProfile] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('Vous devez être connecté pour accéder à cet espace.');
      navigate('/login');
      return;
    }
    try {
      // Extraction des informations du token.
      // En production, il est préférable de récupérer le profil via une API sécurisée.
      const payload = JSON.parse(atob(token.split('.')[1]));
      setProfile(payload);
    } catch (error) {
      console.error("Erreur lors du décodage du token :", error);
      navigate('/login');
    }
  }, [navigate]);

  return (
    <div className="dashboard-container">
      <h1>Espace Utilisateur</h1>
      {profile ? (
        <div className="profile-info">
          <p><strong>Nom :</strong> {profile.name || profile.sub}</p>
          <p><strong>Email :</strong> {profile.email || 'Non renseigné'}</p>
        </div>
      ) : (
        <p>Chargement des informations...</p>
      )}

      <div className="dashboard-links">
        <Link to="/mes-reservations"><button>Mes Réservations</button></Link>
        <Link to="/favoris"><button>Mes Favoris</button></Link>
        <Link to="/modifier-profil"><button>Modifier mon profil</button></Link>
      </div>
    </div>
  );
}

export default DashboardUtilisateurs;
