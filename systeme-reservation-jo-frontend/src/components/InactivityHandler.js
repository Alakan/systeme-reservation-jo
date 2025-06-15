import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

/**
 * Surveille l'activité utilisateur et déconnecte après `timeout` ms d'inactivité.
 * Usage : wrappe ton AppRouter/ta partie protégée.
 */
const InactivityHandler = ({ timeout = 15 * 60 * 1000, children }) => {
  const navigate = useNavigate();

  useEffect(() => {
    let timer;

    const resetTimer = () => {
      clearTimeout(timer);
      timer = setTimeout(() => {
        localStorage.removeItem('token');
        alert("Déconnexion automatique pour cause d'inactivité.");
        navigate('/login', { replace: true });
      }, timeout);
    };

    // Écoute les interactions
    const events = ['click', 'mousemove', 'keydown', 'scroll'];
    events.forEach((e) => window.addEventListener(e, resetTimer));

    resetTimer(); // démarre le timer

    return () => {
      clearTimeout(timer);
      events.forEach((e) => window.removeEventListener(e, resetTimer));
    };
  }, [navigate, timeout]);

  return children;
};

export default InactivityHandler;
