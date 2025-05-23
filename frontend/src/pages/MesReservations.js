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
            navigate("/login");
            return;
        }

        const payload = JSON.parse(atob(token.split('.')[1]));
        const userEmail = payload.sub;
        console.log("Email récupéré :", userEmail);

        api.get(`/reservations/utilisateur?email=${userEmail}`, {
            headers: { Authorization: `Bearer ${token}` }
        })
        .then(response => {
            console.log("Données brutes reçues :", response.data);

            // ✅ Supprime uniquement les entrées non valides (évite d’exclure des réservations correctes)
            const reservationsFiltrees = response.data.filter(reservation => 
                reservation && typeof reservation === 'object' && reservation.id
            );

            // ✅ Corrige la récupération des événements envoyés comme ID au lieu d'objet
            const reservationsCorrigees = reservationsFiltrees.map(reservation => {
                if (!reservation.evenement || typeof reservation.evenement !== 'object') {
                    console.warn(`ID événement trouvé : ${reservation.evenement}. Conversion en objet.`);
                    return {
                        ...reservation,
                        evenement: { 
                            id: reservation.evenement || "ID inconnu", 
                            titre: "Événement non chargé correctement",
                            dateEvenement: "Date inconnue", 
                            lieu: "Lieu indisponible"
                        }
                    };
                }
                return reservation;
            });

            console.log("Réservations après correction :", reservationsCorrigees);
            setReservations(reservationsCorrigees);
        })
        .catch(error => {
            console.error("Erreur API :", error);
            alert("Impossible de récupérer vos réservations.");
        });
    }, [navigate, token]);

    return (
        <div className="reservations-container">
            <h1>Mes Réservations</h1>
            <Link to="/evenements">
                <button className="btn-back">Retour aux événements</button>
            </Link>

            {reservations.length === 0 ? (
                <p>Vous n'avez aucune réservation en cours.</p>
            ) : (
                <ul className="reservations-list">
                    {reservations.map((reservation, index) => (
                        <li key={index} className="reservation-item">
                            <strong>
                                {reservation.evenement && reservation.evenement.titre
                                    ? reservation.evenement.titre
                                    : `Événement inconnu (${reservation.evenement?.id || "ID manquant"})`}
                            </strong> 
                            {reservation.dateReservation 
                                ? new Date(reservation.dateReservation).toLocaleDateString('fr-FR') 
                                : "Données manquantes : date"} 
                            {reservation.nombreBillets > 0 
                                ? reservation.nombreBillets 
                                : "Données manquantes : billets"}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default MesReservations;
