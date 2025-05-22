import { useNavigate } from 'react-router-dom';
import '../styles/Home.css';

function Home() {
    const navigate = useNavigate();

    return (
        <div className="home-container">
            <h1>Bienvenue aux Jeux Olympiques 2025 ! 🎉</h1>
            <p>Explorez les événements et réservez vos places dès maintenant.</p>
            <div className="home-buttons">
                <button onClick={() => navigate("/evenements")}>Voir les événements</button>
                <button className="btn-access" onClick={() => navigate("/login")}>Accéder à mon compte</button>
            </div>
        </div>
    );
}

export default Home;
