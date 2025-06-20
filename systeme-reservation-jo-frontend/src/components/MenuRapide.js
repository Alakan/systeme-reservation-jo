import React, { useState, useContext } from 'react';
import { useNavigate, Link }           from 'react-router-dom';
import { UserContext }                 from '../contexts/UserContext';
import { useCart }                     from '../contexts/CartContext';  // ← import
import Settings                        from './Settings';
import '../styles/MenuRapide.css';

export default function MenuRapide() {
  const [isOpen, setIsOpen]             = useState(false);
  const { isAuthenticated, user, roles, setUser } = useContext(UserContext);
  const { cart }                        = useCart();                   // ← hook
  const navigate                        = useNavigate();

  const cartCount = cart.reduce((sum, i) => sum + i.quantity, 0);

  // Extrait le pseudo (avant @)
  const getDisplayName = () => {
    const login = user?.username || user?.sub || user?.email || '';
    const idx   = login.indexOf('@');
    return idx > 0 ? login.slice(0, idx) : login;
  };

  const goTo = (path) => {
    setIsOpen(false);
    navigate(path, { replace: true });
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    setUser(null);
    goTo('/login');
  };

  const dashboardPath = roles.includes('ADMINISTRATEUR')
    ? '/admin'
    : '/dashboard';

  return (
    <nav className="menu-rapide">
      <button className="menu-button" onClick={() => setIsOpen(o => !o)}>
        ☰
      </button>

      {isAuthenticated && (
        <span className="user-info-top">
          Salut, <strong>{getDisplayName()}</strong> !
        </span>
      )}

      {isOpen && (
        <div className="menu-items">
          <Settings />

          <button onClick={() => goTo('/')}>Accueil</button>
          <button onClick={() => goTo('/evenements')}>Événements</button>

          {/* Lien vers Panier */}
          <Link to="/panier" className="menu-item">
            🛒 Panier ({cartCount})
          </Link>

          {isAuthenticated ? (
            <>
              <button onClick={() => goTo(dashboardPath)}>Dashboard</button>
              {roles.includes('UTILISATEUR') && (
                <button onClick={() => goTo('/mes-reservations')}>
                  Mes réservations
                </button>
              )}
              /*<button onClick={() => goTo('/modifier-profil')}>
                Mon profil
              </button>*/
              <button className="logout-btn" onClick={handleLogout}>
                Déconnexion
              </button>
            </>
          ) : (
            <>
              <button onClick={() => goTo('/login')}>Connexion</button>
              <button onClick={() => goTo('/register')}>Créer un compte</button>
            </>
          )}
        </div>
      )}
    </nav>
  );
}
