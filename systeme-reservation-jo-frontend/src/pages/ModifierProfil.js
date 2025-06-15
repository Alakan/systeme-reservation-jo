// src/pages/ModifierProfil.js
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import '../styles/ModifierProfil.css';

function ModifierProfil() {
  const [profile, setProfile] = useState({
    email: '',
    username: '',
    password: ''          // nouveau mot de passe
  });
  const [message, setMessage] = useState('');
  const [error, setError]     = useState('');
  const navigate = useNavigate();

  // Charge l’email et le username au montage
  useEffect(() => {
    const load = async () => {
      const token = localStorage.getItem('token');
      if (!token) return navigate('/login');
      try {
        const { data } = await api.get('/utilisateurs/me', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setProfile({
          email:    data.email,
          username: data.username,
          password: ''
        });
      } catch {
        alert("Impossible de récupérer le profil.");
      }
    };
    load();
  }, [navigate]);

  const handleChange = (e) =>
    setProfile((p) => ({ ...p, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');

    // 1) On exige un mot de passe non vide
    if (!profile.password.trim()) {
      setError('Le mot de passe ne peut pas être vide.');
      return;
    }

    const token = localStorage.getItem('token');
    if (!token) return navigate('/login');

    try {
      // 2) Appel API : payload minimal (email, username, password)
      await api.put(
        '/utilisateurs/me',
        {
          email:    profile.email,
          username: profile.username,
          password: profile.password
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      // 3) Succès → message + redirection
      setMessage('Profil mis à jour avec succès !');
      setTimeout(() => navigate('/dashboard'), 1000);
    } catch (err) {
      // 4) Échec → message d’erreur et on reste
      if (err.response?.status === 400) {
        setError(err.response.data || 'Mot de passe incorrect.');
      } else {
        setError('Erreur serveur, veuillez réessayer.');
      }
    }
  };

  return (
    <div className="modifier-profil-container">
      <h1>Modifier mon profil</h1>

      {message && <div className="alert success">{message}</div>}
      {error   && <div className="alert error">{error}</div>}

      <form onSubmit={handleSubmit}>
        <label>
          Nom d'utilisateur
          <input
            name="username"
            value={profile.username}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          Email
          <input
            type="email"
            name="email"
            value={profile.email}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          Nouveau mot de passe
          <input
            type="password"
            name="password"
            value={profile.password}
            onChange={handleChange}
            placeholder="Obligatoire pour changer"
            required
          />
        </label>

        <button type="submit">Mettre à jour</button>
      </form>
    </div>
  );
}

export default ModifierProfil;
