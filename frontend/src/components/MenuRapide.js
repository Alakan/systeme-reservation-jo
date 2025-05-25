// src/components/MenuRapide.js
import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/MenuRapide.css';

function MenuRapide() {
  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();
  const isAuthenticated = !!localStorage.getItem("token"); // Vérifie si un utilisateur est connecté

  const handleLogout = () => {
    localStorage.removeItem("token");
    console.log("Déconnexion effectuée, token supprimé :", localStorage.getItem("token"));
    navigate("/login");
  };

  // Redirige vers la page de login si on tente d'accéder à une page protégée sans être connecté
  useEffect(() => {
    const token = localStorage.getItem("token");
    console.log("Vérification de connexion, token :", token);
    const pagesProtegees = ["/reservations", "/profil", "/dashboard", "/mes-reservations"];
    if (!token && pagesProtegees.includes(window.location.pathname)) {
      navigate("/login");
    }
  }, [navigate]);

  return (
    <nav className="menu-rapide">
      <button className="menu-button" onClick={() => setIsOpen(!isOpen)}>☰</button>
      
      {isOpen && (
        <div className="menu-items">
          <button onClick={() => navigate("/")}>Accueil</button>
          <button onClick={() => navigate("/evenements")}>Événements</button>
          {isAuthenticated ? (
            <>
              <button onClick={() => navigate("/dashboard")}>Dashboard</button>
              <button onClick={() => navigate("/mes-reservations")}>Mes Réservations</button>
              <button onClick={handleLogout}>Déconnexion</button>
            </>
          ) : (
            <>
              <button onClick={() => navigate("/login")}>Mon compte</button>
              <Link to="/register">
                <button>Créer un compte</button>
              </Link>
            </>
          )}
        </div>
      )}
    </nav>
  );
}

export default MenuRapide;
