import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';
import '../styles/Evenements.css';

function Evenements() {
    const [evenements, setEvenements] = useState([]);
    const navigate = useNavigate(); 

    useEffect(() => {
        const token = localStorage.getItem("token");

        api.get('/evenements', { headers: token ? { Authorization: `Bearer ${token}` } : {} })
            .then(response => {
                if (Array.isArray(response.data)) {
                    setEvenements(response.data);
                } else {
                    console.error("Réponse API inattendue :", response.data);
                }
            })
            .catch(error => console.error("Erreur lors de la récupération des événements :", error));
    }, []);

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString('fr-FR', {
            year: 'numeric', month: 'long', day: 'numeric', 
            hour: '2-digit', minute: '2-digit' 
        });
    };

    const handleReservation = async (evenement) => {
        const token = localStorage.getItem("token");

        if (!token) {
            alert("Vous devez être connecté pour réserver.");
            window.location.href = "/login";
            return;
        }

        const payload = JSON.parse(atob(token.split('.')[1]));
        const userEmail = payload.sub;

        try {
            const response = await api.post(`/reservations`, { 
                utilisateur: { email: userEmail },
                evenement, 
                dateReservation: new Date().toISOString(), 
                nombreBillets: 1 
            }, { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } });

            // eslint-disable-next-line no-unused-vars
            const reservationId = response.data.id;
            console.log("Reservation créée avec ID :", reservationId);

            // ✅ Demande du mode de paiement via `window.prompt`
            const modePaiement = window.prompt("Choisissez votre mode de paiement : CARTE, PAYPAL, VIREMENT")?.toUpperCase();
            
            if (modePaiement && ["CARTE", "PAYPAL", "VIREMENT"].includes(modePaiement)) {
                handlePaiement(reservationId, modePaiement);
            } else {
                alert("Mode de paiement invalide. Veuillez réessayer.");
            }

            alert(`Réservation effectuée pour ${evenement.titre} !`);
        } catch (error) {
            console.error("Erreur lors de la réservation :", error);
        }
    };

    const handlePaiement = (reservationId, modePaiement) => {
        console.log("Paiement en cours pour réservation ID :", reservationId, "Mode :", modePaiement);

        const token = localStorage.getItem('token');
        if (!token) {
            alert("Erreur : vous devez être connecté.");
            return;
        }

        api.put(`/reservations/${reservationId}/paiement`, JSON.stringify(modePaiement), {
            headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
        })
        .then(() => {
            alert("Paiement effectué avec succès !");
            navigate("/reservations");
        })
        .catch(error => console.error("Erreur complète lors du paiement :", error));
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
