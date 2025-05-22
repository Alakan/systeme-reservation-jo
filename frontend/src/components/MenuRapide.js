import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/MenuRapide.css';

function MenuRapide() {
    const [isOpen, setIsOpen] = useState(false);
    const navigate = useNavigate(); // Navigation dynamique
    const isAuthenticated = !!localStorage.getItem("token"); // Vérifie si un utilisateur est connecté

    const handleLogout = () => {
        localStorage.removeItem("token"); // Supprime le token d'authentification
        console.log("Déconnexion effectuée, token supprimé :", localStorage.getItem("token")); // Vérification immédiate
        navigate("/login"); // Redirige vers la page de connexion
    };

    // Redirige uniquement si l'utilisateur essaie d'accéder à une page protégée sans être connecté
    useEffect(() => {
        const token = localStorage.getItem("token");
        console.log("Vérification de connexion, token :", token);

        const pagesProtegees = ["/reservations", "/profil", "/dashboard"]; // Définir les pages protégées
        if (!token && pagesProtegees.includes(window.location.pathname)) {
            navigate("/login"); // Redirige seulement pour les pages protégées
        }
    }, [navigate]);

    return (
        <div className="menu-rapide">
            <button className="menu-button" onClick={() => setIsOpen(!isOpen)}>☰</button>

            {isOpen && (
                <div className="menu-items">
                    <button onClick={() => navigate("/")}>Accueil</button>
                    <button onClick={() => navigate("/evenements")}>Événements</button>
                    <button onClick={() => navigate("/login")}>Mon compte</button>
                    {isAuthenticated && <button onClick={() => navigate("/mes-reservations")}>Mes Réservations</button>} {/* Nouvel ajout */}
                    {isAuthenticated && <button onClick={handleLogout}>Déconnexion</button>} {/* Affiché uniquement si connecté */}
                </div>
            )}
        </div>
    );
}

export default MenuRapide;
