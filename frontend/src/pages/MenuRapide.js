import { useState } from 'react';
import { BrowserRouter, Link } from 'react-router-dom'; // ✅ Encapsulation du routeur
import '../styles/MenuRapide.css';

function MenuRapide() {
    const [isOpen, setIsOpen] = useState(false);

    return (
        <BrowserRouter> {/* ✅ Ajout de l’encapsulation */}
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
        </BrowserRouter>
    );
}

export default MenuRapide;
