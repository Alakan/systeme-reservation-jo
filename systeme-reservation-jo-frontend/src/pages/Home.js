import React from 'react';
import { useNavigate } from 'react-router-dom';
import Footer          from '../components/Footer';
import '../styles/Home.css';

export default function Home() {
  const navigate = useNavigate();

  return (
    <>
      <header className="hero">
        <h1>Bienvenue aux Jeux Olympiques 2024 ! 🎉</h1>
        <p>
          Ne manquez pas les grands événements&nbsp;! Découvrez les compétitions
          et réservez votre place dès maintenant.
        </p>
        <div className="hero-buttons">
          <button
            className="btn-primary"
            onClick={() => navigate('/evenements')}
          >
            Voir les événements
          </button>
          <button
            className="btn-access"
            onClick={() => navigate('/login')}
          >
            Accéder à mon compte
          </button>
        </div>
      </header>

      <main className="home-container">
        {/* Tu peux ajouter ici d’autres sections (news, spotlight, etc.) */}
      </main>

      <Footer />
    </>
  );
}
