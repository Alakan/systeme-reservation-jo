import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';
import '../styles/MesReservations.css';

function MesReservations() {
    const [reservations, setReservations] = useState([]);
    const navigate = useNavigate();
    const token = localStorage.getItem("token");

    useEffect(() => {
        if (!token) {
            navigate("/login"); // ✅ Redirige si l’utilisateur n'est pas connecté
            return;
        }

        // ✅ Vérifier que l’email est bien récupéré
        const payload = JSON.parse(atob(token.split('.')[1]));
        const userEmail = payload.sub;
        console.log("🔍 Email récupéré :", userEmail);

        // ✅ Vérifier la réponse API pour s’assurer que les réservations sont bien renvoyées
        api.get(`/reservations/utilisateur?email=${userEmail}`, {
            headers: { Authorization: `Bearer ${token}` }
        })
        .then(response => {
            console.log("🔍 Réponse brute API :", response);
            console.log("🔍 Réservations récupérées :", response.data);

            if (Array.isArray(response.data) && response.data.length > 0) {
                setReservations(response.data);
            } else {
                console.warn("⚠ Aucune réservation trouvée pour cet utilisateur !");
            }
        })
        .catch(error => {
            console.error("🚨 Erreur API :", error);
            alert("⚠ Impossible de récupérer vos réservations. Veuillez réessayer plus tard.");
        });
    }, [navigate, token]);

    return (
        <div className="reservations-container">
            <h1>📋 Mes Réservations</h1>
            <Link to="/evenements"><button className="btn-back">Retour aux événements</button></Link>

            {reservations.length === 0 ? (
                <p>Vous n'avez aucune réservation en cours.</p>
            ) : (
                <ul className="reservations-list">
                    {reservations.map((reservation) => (
                        <li key={reservation.id} className="reservation-item">
                            <strong>{reservation.evenement.titre}</strong> 📅 {new Date(reservation.dateReservation).toLocaleDateString('fr-FR')}  
                            🎟 {reservation.nombreBillets} billet(s)
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default MesReservations;
