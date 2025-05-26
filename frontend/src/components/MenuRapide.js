// src/components/MenuRapide.js
import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/MenuRapide.css';

function MenuRapide() {
  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();
  const isAuthenticated = !!localStorage.getItem("token");

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  useEffect(() => {
    const token = localStorage.getItem("token");
    const pagesProtegees = ["/reservations", "/modifier-profil", "/dashboard", "/mes-reservations"];
    if (!token && pagesProtegees.includes(window.location.pathname)) {
      navigate("/login");
    }
  }, [navigate]);

  return (
    <nav className="menu-rapide">
      <button className="menu-button" onClick={() => setIsOpen(!isOpen)}>☰</button>
      {isOpen && (
        <div className="menu-items">
          <button onClick={() => { setIsOpen(false); navigate("/") }}>Accueil</button>
          <button onClick={() => { setIsOpen(false); navigate("/evenements") }}>Événements</button>
          {isAuthenticated ? (
            <>
              <button onClick={() => { setIsOpen(false); navigate("/dashboard") }}>Dashboard</button>
              <button onClick={() => { setIsOpen(false); navigate("/mes-reservations") }}>Mes Réservations</button>
              {/* Lien vers modifier le profil */}
              <button onClick={() => { setIsOpen(false); navigate("/modifier-profil") }}>Modifier mon profil</button>
              <button onClick={() => { setIsOpen(false); handleLogout(); }}>Déconnexion</button>
            </>
          ) : (
            <>
              <button onClick={() => { setIsOpen(false); navigate("/login"); }}>Mon compte</button>
              <Link to="/register">
                <button onClick={() => setIsOpen(false)}>Créer un compte</button>
              </Link>
            </>
          )}
        </div>
      )}
    </nav>
  );
}

export default MenuRapide;
