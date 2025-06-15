import React, { useState, useContext } from 'react';
import { useNavigate, Link }           from 'react-router-dom';
import api                             from '../services/api';
import { UserContext }                 from '../contexts/UserContext';
import '../styles/Login.css';

export default function Login() {
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [error, setError]       = useState('');
  const { setUser }             = useContext(UserContext);
  const navigate                = useNavigate();

  const handleLogin = async e => {
    e.preventDefault();
    setError('');
    try {
      const { data } = await api.post('auth/login', { email, password });
      localStorage.setItem('token', data.token);

      const payload = JSON.parse(atob(data.token.split('.')[1]));
      setUser(payload);

      // redirection selon rôle
      if (payload.roles?.includes('ADMINISTRATEUR')) {
        navigate('/admin', { replace: true });
      } else {
        navigate('/dashboard', { replace: true });
      }
    } catch (err) {
      setError(
        err.response?.data?.message ||
        'Échec de la connexion. Vérifiez vos identifiants.'
      );
    }
  };

  return (
    <div className="login-container">
      <h1>Connexion</h1>
      {error && <div className="alert alert-danger">{error}</div>}
      <form onSubmit={handleLogin}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={e => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Mot de passe"
          value={password}
          onChange={e => setPassword(e.target.value)}
          required
        />
        <button type="submit">Se connecter</button>
      </form>
      <p>
        Pas encore de compte ? <Link to="/register">Inscrivez-vous</Link>
      </p>
      <Link to="/"><button className="btn-home">Accueil</button></Link>
    </div>
  );
}
