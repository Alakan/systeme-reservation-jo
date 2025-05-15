import { useState } from 'react';
import api from '../services/api';

function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleLogin = (e) => {
        e.preventDefault();
        api.post('/auth/login', { email, password })
            .then(response => {
                localStorage.setItem('token', response.data.token);
                alert("Connexion réussie !");
            })
            .catch(error => {
                console.error("Erreur de connexion :", error);
                alert("Email ou mot de passe incorrect.");
            });
    };

    return (
        <div>
            <h1>Connexion</h1>
            <form onSubmit={handleLogin}>
                <div>
                    <label>Email :</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Mot de passe :</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Se connecter</button>
            </form>
        </div>
    );
}

export default Login;
