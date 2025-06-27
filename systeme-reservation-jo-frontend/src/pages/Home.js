// src/pages/Home.js
import React, { useState, useEffect, useContext } from 'react';
import { useNavigate }                         from 'react-router-dom';
import api                                     from '../services/api';
import Footer                                  from '../components/Footer';
import { UserContext }                         from '../contexts/UserContext';
import '../styles/Home.css';

export default function Home() {
  const navigate = useNavigate();
  const { user, roles } = useContext(UserContext);

  // 1) COMPTE À REBOURS
  const [countdown, setCountdown] = useState({ d: 0, h: 0, m: 0, s: 0 });
  useEffect(() => {
    const targetDate = new Date('2025-07-26T20:00:00');
    const tick = () => {
      const diff = targetDate - new Date();
      if (diff <= 0) {
        clearInterval(timer);
        return;
      }
      const d = Math.floor(diff / 86400000);
      const h = Math.floor((diff % 86400000) / 3600000);
      const m = Math.floor((diff % 3600000) / 60000);
      const s = Math.floor((diff % 60000) / 1000);
      setCountdown({ d, h, m, s });
    };
    const timer = setInterval(tick, 1000);
    tick();
    return () => clearInterval(timer);
  }, []);

  // 2) ÉVÉNEMENTS À LA UNE
  const [featured, setFeatured] = useState([]);
  useEffect(() => {
    api
      .get('evenements?limit=4')
      .then(res => setFeatured(res.data))
      .catch(() => setFeatured([]));
  }, []);

  // 3) NEWSLETTER
  const [email, setEmail] = useState('');
  const handleSubscribe = e => {
    e.preventDefault();
    alert(`Merci ! L’email ${email} a bien été enregistré.`);
    setEmail('');
  };

  // 4) Accès au compte selon rôle
  const handleAccountAccess = () => {
    if (!user) {
      navigate('/login');
    } else if (roles.includes('ADMINISTRATEUR')) {
      navigate('/admin');
    } else {
      navigate('/dashboard');
    }
  };

  return (
    <>
      <header className="hero">
        <h1>Bienvenue aux Jeux Olympiques 2025 ! 🎉</h1>
        <p>
          Ne manquez pas les grands événements ! Réservez votre place dès
          maintenant.
        </p>
        <div className="hero-buttons">
          <button
            className="btn-primary"
            onClick={() => navigate('/evenements')}
          >
            Voir les événements
          </button>
          <button className="btn-access" onClick={handleAccountAccess}>
            Accéder à mon compte
          </button>
        </div>
      </header>

      <main className="home-container">
        {/* — Compte à rebours — */}
        <section className="countdown">
          <h2>Compte à rebours</h2>
          <div className="timer">
            <div>
              <strong>{countdown.d}</strong>
              <span>Jours</span>
            </div>
            <div>
              <strong>{countdown.h}</strong>
              <span>Heures</span>
            </div>
            <div>
              <strong>{countdown.m}</strong>
              <span>Minutes</span>
            </div>
            <div>
              <strong>{countdown.s}</strong>
              <span>Secondes</span>
            </div>
          </div>
        </section>

        {/* — Événements à la une — */}
        <section className="featured-events">
          <h2>Événements à la une</h2>
          <div className="cards">
            {featured.length === 0 && <p>Chargement des événements…</p>}
            {featured.map(ev => (
              <div key={ev.id} className="card">
                <img src={ev.imageUrl} alt={ev.titre} />
                <h3>{ev.titre}</h3>
                <p>
                  {new Date(ev.dateEvenement).toLocaleDateString('fr-FR')}
                </p>
                <button
                  onClick={() =>
                    navigate(`/reservation?event=${ev.id}`)
                  }
                >
                  Réserver
                </button>
              </div>
            ))}
          </div>
        </section>

        {/* — Newsletter — */}
        <section className="newsletter">
          <h2>Restez informé·e·s</h2>
          <form onSubmit={handleSubscribe}>
            <input
              type="email"
              placeholder="Votre email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
            />
            <button type="submit">S’abonner</button>
          </form>
        </section>
      </main>

      <Footer />
    </>
  );
}
