// src/pages/ModifierProfil.js
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import '../styles/ModifierProfil.css';

function ModifierProfil() {
  const [profile, setProfile] = useState({
    email: '',
    username: '',
    password: ''
  });
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // 1) charger les infos au montage
  useEffect(() => {
    const loadProfile = async () => {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('Vous devez être connecté.');
        navigate('/login');
        return;
      }
      try {
        const { data } = await api.get('/utilisateurs/me', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setProfile({
          email: data.email || '',
          username: data.username || '',
          password: ''
        });
      } catch (err) {
        console.error(err);
        alert("Impossible de récupérer le profil.");
      }
    };
    loadProfile();
  }, [navigate]);

  const handleChange = (e) => {
    setProfile((p) => ({ ...p, [e.target.name]: e.target.value }));
  };

  // 2) soumettre la mise à jour
  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage(null);
    setError(null);

    const token = localStorage.getItem('token');
    if (!token) {
      alert('Vous devez être connecté.');
      navigate('/login');
      return;
    }

    // Préparer le payload : n'ajoute password que s'il est non vide
    const payload = {
      email: profile.email,
      username: profile.username
    };
    if (profile.password.trim() !== '') {
      payload.password = profile.password;
    }

    try {
      await api.put('/utilisateurs/me', payload, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setMessage('Profil mis à jour avec succès !');
      // tu peux éventuellement forcer la déconnexion pour regénérer un token
      setTimeout(() => navigate('/dashboard'), 1000);
    } catch (err) {
      console.error(err);
      if (err.response?.status === 400) {
        setError('Mot de passe actuel incorrect ou données invalides.');
      } else {
        setError("Erreur serveur, veuillez réessayer plus tard.");
      }
    }
  };

  return (
    <div className="modifier-profil-container">
      <h1>Modifier mon profil</h1>
      {message && <div className="alert success">{message}</div>}
      {error   && <div className="alert error">{error}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Nom d'utilisateur</label>
          <input
            type="text"
            name="username"
            value={profile.username}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label>Email</label>
          <input
            type="email"
            name="email"
            value={profile.email}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label>Nouveau mot de passe</label>
          <input
            type="password"
            name="password"
            value={profile.password}
            onChange={handleChange}
            placeholder="Laissez vide pour ne pas changer"
          />
        </div>

        <button type="submit">Mettre à jour</button>
      </form>
    </div>
  );
}

export default ModifierProfil;
