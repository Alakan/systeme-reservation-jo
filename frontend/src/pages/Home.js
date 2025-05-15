import { Link } from 'react-router-dom';
import '../styles/Home.css';

function Home() {
    return (
        <div className="home-container">
            <h1>Bienvenue aux Jeux Olympiques 2025 ! 🎉</h1>
            <p>Explorez les événements et réservez vos places dès maintenant.</p>
            <div className="home-buttons">
                <Link to="/evenements"><button>Voir les événements</button></Link>
                <Link to="/register"><button>Créer un compte</button></Link>
                <Link to="/login"><button className="btn-login">Se connecter</button></Link> {/* ✅ Ajout du bouton */}
            </div>
        </div>
    );
}

export default Home;
