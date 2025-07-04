// src/pages/ModifierProfil.js
import { useState, useEffect } from 'react';
import { useNavigate }       from 'react-router-dom';
import api                    from '../services/api';
import '../styles/ModifierProfil.css';

function ModifierProfil() {
  const [profile, setProfile] = useState({
    email: '',
    username: '',
    currentPassword: '',
    newPassword: ''
  });
  const [message, setMessage] = useState('');
  const [error, setError]     = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const load = async () => {
      const token = localStorage.getItem('token');
      if (!token) return navigate('/login');
      try {
        const { data } = await api.get('/utilisateurs/me', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setProfile(p => ({
          ...p,
          email:    data.email,
          username: data.username
        }));
      } catch {
        alert("Impossible de récupérer le profil.");
      }
    };
    load();
  }, [navigate]);

  const handleChange = (e) =>
    setProfile(p => ({ ...p, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage(''); 
    setError('');

    const token = localStorage.getItem('token');
    if (!token) return navigate('/login');

    // 1) Validation côté client des champs mot de passe
    const hasOld = profile.currentPassword.trim() !== '';
    const hasNew = profile.newPassword.trim() !== '';

    if (hasOld !== hasNew) {
      setError('Pour changer le mot de passe, remplissez les deux champs.');
      return;
    }

    // 2) Construction du payload
    const payload = {
      email:    profile.email,
      username: profile.username
    };
    if (hasOld && hasNew) {
      payload.currentPassword = profile.currentPassword;
      payload.newPassword     = profile.newPassword;
    }

    try {
      await api.put('/utilisateurs/me', payload, {
        headers: { Authorization: `Bearer ${token}` }
      });

      setMessage('Profil mis à jour avec succès !');

      // 3) Redirection *uniquement* si mot de passe changé
      if (hasNew) {
        setTimeout(() => navigate('/dashboard'), 1000);
      }
    } catch (err) {
      if (err.response?.status === 400) {
        setError(err.response.data || 'Ancien mot de passe incorrect.');
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

      <form className="modifier-profil-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Nom d'utilisateur</label>
          <input
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
          <label>Ancien mot de passe</label>
          <input
            type="password"
            name="currentPassword"
            value={profile.currentPassword}
            onChange={handleChange}
            placeholder="Obligatoire pour changer"
          />
        </div>

        <div className="form-group">
          <label>Nouveau mot de passe</label>
          <input
            type="password"
            name="newPassword"
            value={profile.newPassword}
            onChange={handleChange}
            placeholder="Laissez vide pour ne pas changer"
          />
        </div>

        <button className="btn btn-submit" type="submit">
          Mettre à jour
        </button>
      </form>
    </div>
  );
}

export default ModifierProfil;
