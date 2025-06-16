import React, { useState, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { UserContext }       from '../contexts/UserContext';
import '../styles/MenuRapide.css';

export default function MenuRapide() {
  const [isOpen, setIsOpen]        = useState(false);
  const { isAuthenticated, roles } = useContext(UserContext);
  const navigate                   = useNavigate();

  const dashboardPath = roles.includes('ADMINISTRATEUR')
    ? '/admin'
    : '/dashboard';

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login', { replace: true });
  };

  return (
    <nav className="menu-rapide">
      <button
        className="menu-button"
        onClick={() => setIsOpen((o) => !o)}
      >
        ☰
      </button>

      {isAuthenticated && (
        <span className="user-info-top">
          Salut, <strong>
            {roles.includes('ADMINISTRATEUR') ? 'Admin' : 'User'}
          </strong> !
        </span>
      )}

      {isOpen && (
        <div className="menu-items">
          <Link to="/">Accueil</Link>
          <Link to="/evenements">Événements</Link>

          {isAuthenticated ? (
            <>
              <Link to={dashboardPath}>Dashboard</Link>
              {roles.includes('UTILISATEUR') && (
                <Link to="/mes-reservations">Mes réservations</Link>
              )}
              <Link to="/modifier-profil">Mon profil</Link>
              <button className="logout-btn" onClick={handleLogout}>
                Déconnexion
              </button>
            </>
          ) : (
            <>
              <Link to="/login">Connexion</Link>
              <Link to="/register">Créer un compte</Link>
            </>
          )}
        </div>
      )}
    </nav>
  );
}
