import { Link } from 'react-router-dom';
import { useState } from 'react';
import api from '../services/api';
import '../styles/Login.css';

function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        console.log("Tentative de connexion :", { email, password });

        try {
            const response = await api.post('/auth/login', JSON.stringify({ email, password }), {
                headers: { 'Content-Type': 'application/json' }
            });

            const token = response.data.token;
            localStorage.setItem("token", token); // ✅ Stockage du token
            console.log("Token sauvegardé :", localStorage.getItem("token")); // ✅ Vérification console

            alert("Connexion réussie !");
            window.location.href = "/evenements"; // 🔹 Redirection après succès
        } catch (error) {
            console.error("Erreur de connexion :", error.response?.data || error);
            alert("Échec de la connexion. Vérifie tes informations.");
        }
    };

    return (
        <div className="login-container">
            <h1>Connexion</h1>
            <form onSubmit={handleLogin}>
                <input type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                <input type="password" placeholder="Mot de passe" value={password} onChange={(e) => setPassword(e.target.value)} required />
                <button type="submit">Se connecter</button>
            </form>

            {/* ✅ Ajout du bouton de retour à la page principale */}
            <Link to="/"><button className="btn-home">Retour à l’accueil</button></Link>
        </div>
    );
}

export default Login;
