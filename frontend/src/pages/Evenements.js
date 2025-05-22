import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';
import '../styles/Evenements.css';

function Evenements() {
    const [evenements, setEvenements] = useState([]);

    useEffect(() => {
        const token = localStorage.getItem("token");

        api.get('/evenements', { headers: token ? { Authorization: `Bearer ${token}` } : {} })
            .then(response => setEvenements(response.data))
            .catch(error => console.error("Erreur lors de la récupération des événements :", error));
    }, []);

    const formatDate = (dateString) => {
        const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
        return new Date(dateString).toLocaleDateString('fr-FR', options);
    };

    const handleReservation = (evenement) => { // ✅ Correction : On passe l'objet événement entier
        const token = localStorage.getItem("token");

        if (!token) {
            alert("Vous devez être connecté pour réserver.");
            window.location.href = "/login";
            return;
        }

        try {
            const payload = JSON.parse(atob(token.split('.')[1])); // ✅ Extraction du payload du token
            const userEmail = payload.sub; // ✅ Récupération de l'email de l'utilisateur

            api.post(`/reservations`, { 
                utilisateur: { email: userEmail },  // ✅ Vérification que l’utilisateur est bien envoyé
                evenement, 
                dateReservation: new Date().toISOString(), 
                nombreBillets: 1 
            }, { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } })
                .then(() => alert(`Réservation effectuée pour ${evenement.titre} !`))
                .catch(error => {
                    console.error("Erreur lors de la réservation :", error.response?.data || error);
                    alert("Une erreur est survenue lors de la réservation. Vérifiez les informations envoyées.");
                });
        } catch (error) {
            console.error("Erreur de décodage du token :", error);
            alert("Erreur d'authentification. Veuillez vous reconnecter.");
            localStorage.removeItem("token");
            window.location.href = "/login";
        }
    };

    return (
        <div className="evenements-container">
            <h1>Événements disponibles</h1>
            <Link to="/"><button className="btn-home">Retour à la page principale</button></Link>

            {evenements.length === 0 ? (
                <p>Aucun événement disponible.</p>
            ) : (
                <ul className="evenements-list">
                    {evenements.map((evenement) => (
                        <li key={evenement.id} className="evenement-item">
                            <strong>{evenement.titre}</strong> - {formatDate(evenement.dateEvenement)} - {evenement.lieu}
                            <button onClick={() => handleReservation(evenement)}>Réserver</button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default Evenements;
