import { useState, useEffect } from 'react';
import api from '../services/api';
import '../styles/Evenements.css';

function Evenements() {
    const [evenements, setEvenements] = useState([]);

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (!token) {
            alert("Vous devez être connecté pour voir les événements.");
            window.location.href = "/login";
            return;
        }

        api.get('/evenements', { headers: { Authorization: `Bearer ${token}` } })
            .then(response => setEvenements(response.data))
            .catch(error => console.error("Erreur lors de la récupération des événements :", error));
    }, []);

    const formatDate = (dateString) => {
        const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
        return new Date(dateString).toLocaleDateString('fr-FR', options);
    };

    const handleReservation = (id) => {
        const token = localStorage.getItem("token");
        api.post(`/reservations`, { evenementId: id }, { headers: { Authorization: `Bearer ${token}` } })
            .then(() => alert("Réservation effectuée !"))
            .catch(error => console.error("Erreur lors de la réservation :", error));
    };

    return (
        <div className="evenements-container">
            <h1>Événements disponibles</h1>
            {evenements.length === 0 ? (
                <p>Aucun événement disponible.</p>
            ) : (
                <ul className="evenements-list">
                    {evenements.map((evenement) => (
                        <li key={evenement.id} className="evenement-item">
                            <strong>{evenement.titre}</strong> - {formatDate(evenement.dateEvenement)} - {evenement.lieu}
                            <button onClick={() => handleReservation(evenement.id)}>Réserver</button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default Evenements;
