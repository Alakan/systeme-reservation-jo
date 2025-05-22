import { useState, useEffect } from 'react';
import api from '../services/api';
import '../styles/Reservation.css';

function Reservation() {
    const [reservations, setReservations] = useState([]);
    const [loadingPayment, setLoadingPayment] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert("Vous devez être connecté pour voir vos réservations.");
            window.location.href = "/login";
            return;
        }

        api.get('/reservations', { headers: { Authorization: `Bearer ${token}` } })
            .then(response => {
                console.log("Données de réservation :", response.data);
                setReservations(response.data);
            })
            .catch(error => console.error("Erreur lors de la récupération des réservations :", error));
    }, []);

    // ✅ Fonction pour gérer le paiement
    const handlePaiement = (reservationId, modePaiement) => {
        console.log("Demande de paiement pour la réservation ID :", reservationId, "Mode :", modePaiement);
        setLoadingPayment(reservationId);

        const token = localStorage.getItem('token');
        api.put(`/reservations/${reservationId}/paiement`, modePaiement, {
            headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'text/plain' }
        })
        .then(() => {
            alert("Paiement effectué avec succès !");
            setReservations(prevReservations =>
                prevReservations.map(res =>
                    res.id === reservationId ? { ...res, statut: 'CONFIRMEE', modePaiement } : res
                )
            );
        })
        .catch(error => {
            console.error("Erreur lors du paiement :", error.response?.data || error);
            alert("Une erreur est survenue lors du paiement. Vérifiez votre connexion ou réessayez.");
        })
        .finally(() => setLoadingPayment(null));
    };

    return (
        <div className="reservation-container">
            <h1>Vos réservations</h1>
            {reservations.length === 0 ? (
                <p>Aucune réservation trouvée.</p>
            ) : (
                <ul className="reservation-list">
                    {reservations.map((res, index) => (
                        <li key={res.id ?? index}>
                            {/* ✅ Vérification avant d'afficher `evenement.titre` */}
                            <strong>{res.evenement?.titre ?? "Événement inconnu"}</strong> - 
                            {res.evenement?.dateEvenement ?? "Date inconnue"} - 
                            {res.evenement?.lieu ?? "Lieu inconnu"}
                            
                            <p><strong>Statut :</strong> {res.statut}</p>
                            
                            {/* ✅ Afficher le bouton "Payer" seulement si la réservation est en attente */}
                            {res.statut === "EN_ATTENTE" && (
                                <div>
                                    <button 
                                        onClick={() => handlePaiement(res.id, "CARTE")}
                                        disabled={loadingPayment === res.id}
                                    >
                                        {loadingPayment === res.id ? "Paiement en cours..." : "Payer par carte"}
                                    </button>
                                    <button 
                                        onClick={() => handlePaiement(res.id, "PAYPAL")}
                                        disabled={loadingPayment === res.id}
                                    >
                                        {loadingPayment === res.id ? "Paiement en cours..." : "Payer par PayPal"}
                                    </button>
                                    <button 
                                        onClick={() => handlePaiement(res.id, "VIREMENT")}
                                        disabled={loadingPayment === res.id}
                                    >
                                        {loadingPayment === res.id ? "Paiement en cours..." : "Payer par virement"}
                                    </button>
                                </div>
                            )}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default Reservation;
