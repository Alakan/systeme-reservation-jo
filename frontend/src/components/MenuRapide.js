import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/MenuRapide.css';

function MenuRapide() {
    const [isOpen, setIsOpen] = useState(false);
    const navigate = useNavigate(); // ✅ Navigation dynamique

    return (
        <div className="menu-rapide">
            <button className="menu-button" onClick={() => setIsOpen(!isOpen)}>☰</button>

            {isOpen && (
                <div className="menu-items">
                    <button onClick={() => navigate("/")}>Accueil</button>
                    <button onClick={() => navigate("/evenements")}>Événements</button>
                    <button onClick={() => navigate("/login")}>Mon compte</button>
                </div>
            )}
        </div>
    );
}

export default MenuRapide;
