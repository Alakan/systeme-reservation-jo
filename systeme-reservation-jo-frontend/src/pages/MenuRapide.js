import { useState } from 'react';
import { Link } from 'react-router-dom';
import '../styles/MenuRapide.css';

function MenuRapide() {
    const [isOpen, setIsOpen] = useState(false);

    return (
        <div className="menu-rapide">
            <button className="menu-button" onClick={() => setIsOpen(!isOpen)}>☰</button>
            {isOpen && (
                <div className="menu-items">
                    <Link to="/evenements">Événements</Link>
                    <Link to="/login">Mon compte</Link>
                    <Link to="/">Accueil</Link>
                </div>
            )}
        </div>
    );
}

export default MenuRapide;
