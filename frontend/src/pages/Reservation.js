import { useState, useEffect } from 'react';
import api from '../services/api';
import '../styles/Reservation.css';

function Reservation() {
    const [reservations, setReservations] = useState([]);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert("Vous devez être connecté pour voir vos réservations.");
            window.location.href = "/login";
            return;
        }

        api.get('/reservations', { headers: { Authorization: `Bearer ${token}` } })
            .then(response => setReservations(response.data))
            .catch(error => console.error("Erreur lors de la récupération des réservations :", error));
    }, []);

    return (
        <div className="reservation-container">
            <h1>Vos réservations</h1>
            {reservations.length === 0 ? (
                <p>Aucune réservation trouvée.</p>
            ) : (
                <ul className="reservation-list">
                    {reservations.map((res) => (
                        <li key={res.id}>
                            <strong>{res.evenement.titre}</strong> - {res.evenement.dateEvenement} - {res.evenement.lieu}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default Reservation;
