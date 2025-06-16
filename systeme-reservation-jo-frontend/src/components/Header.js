import { useContext }    from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { UserContext }   from '../contexts/UserContext';
import '../styles/Header.css';

export function Header() {
  const { user, setUser } = useContext(UserContext);
  const navigate          = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    setUser(null);
    navigate('/login');
  };

  return (
    <header className="main-header">
      <nav className="nav-links">
        <Link to="/evenements">Événements</Link>
        {user && <Link to="/mes-reservations">Mes réservations</Link>}
      </nav>
      <div className="user-panel">
        {user ? (
          <>
            <span>Salut, <strong>{user.username || user.sub}</strong> !</span>
            <button className="btn-logout" onClick={handleLogout}>
              Déconnexion
            </button>
          </>
        ) : (
          <Link to="/login" className="btn-login">
            Connexion
          </Link>
        )}
      </div>
    </header>
  );
}
