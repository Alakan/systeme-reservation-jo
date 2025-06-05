// src/pages/MesReservations.js
import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';
import '../styles/MesReservations.css';

function MesReservations() {
  const [reservations, setReservations] = useState([]);
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const formatPrice = (price) => {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(price);
  };

  // Fonction de récupération des réservations pour l'utilisateur
  const fetchReservations = () => {
    if (!token) {
      navigate("/login");
      return;
    }

    let userEmail = "";
    try {
      // Extraction du payload pour récupérer l'email (ou un identifiant) de l'utilisateur
      const payload = JSON.parse(atob(token.split('.')[1]));
      userEmail = payload.email || payload.sub;
      console.log("Email récupéré :", userEmail);
    } catch (error) {
      console.error("Erreur lors du décodage du token :", error);
      alert("Erreur d'authentification. Veuillez vous reconnecter.");
      navigate("/login");
      return;
    }

    // Récupère toutes les réservations de l'utilisateur via l'API
    api.get(`/reservations/utilisateur?email=${encodeURIComponent(userEmail)}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(response => {
        console.log("Données brutes reçues :", response.data);

        // Filtrer uniquement les réservations valides (ayant un id)
        const reservationsFiltrees = Array.isArray(response.data)
          ? response.data.filter(reservation =>
              reservation && typeof reservation === 'object' && reservation.id
            )
          : [];

        // Pour chaque réservation, si "evenement" n'est pas un objet, tenter de récupérer ses détails via un appel supplémentaire
        Promise.all(
          reservationsFiltrees.map(async reservation => {
            if (!reservation.evenement || typeof reservation.evenement !== 'object') {
              console.warn(`ID événement trouvé : ${reservation.evenement}. Récupération des détails...`);
              try {
                const eventResponse = await api.get(`/evenements/${reservation.evenement}`, {
                  headers: { Authorization: `Bearer ${token}` }
                });
                return { ...reservation, evenement: eventResponse.data };
              } catch (error) {
                console.error("Erreur lors de la récupération de l'événement :", error);
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
            }
            return reservation;
          })
        )
        .then(updatedReservations => {
          console.log("Réservations après mise à jour :", updatedReservations);
          setReservations(updatedReservations);
        })
        .catch(error => {
          console.error("Erreur lors de la mise à jour des réservations :", error);
          alert("Une erreur est survenue lors du chargement de vos réservations.");
        });
      })
      .catch(error => {
        console.error("Erreur lors de la récupération des réservations :", error.response?.data || error);
        alert("Impossible de récupérer vos réservations.");
      });
  };

  useEffect(() => {
    fetchReservations();
  }, [navigate, token]);

  // Fonction de paiement d'une réservation en attente (non confirmée)
  const handlePayment = async (reservationId) => {
    if (!token) {
      alert("Vous devez être connecté pour payer.");
      navigate("/login");
      return;
    }
    const modePaiementInput = window.prompt("Choisissez votre mode de paiement : CARTE, PAYPAL, VIREMENT");
    const modePaiement = modePaiementInput ? modePaiementInput.toUpperCase() : "";
    if (!["CARTE", "PAYPAL", "VIREMENT"].includes(modePaiement)) {
      alert("Mode de paiement invalide.");
      return;
    }
    try {
      await api.put(
        `/reservations/${reservationId}/paiement`,
        JSON.stringify(modePaiement),
        { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
      );
      alert("Paiement effectué avec succès !");
      fetchReservations();
    } catch (error) {
      console.error("Erreur lors du paiement :", error);
      alert("Erreur lors du paiement.");
    }
  };

  // Fonction pour consulter le billet en cas de réservation confirmée
  const handleVoirBillet = (reservationId) => {
    console.log("Navigation vers le billet pour la réservation ID:", reservationId);
    navigate(`/billet/${reservationId}`);
  };

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
          {reservations.map((reservation) => (
            <li key={reservation.id} className="reservation-item">
              <div className="reservation-details">
                <strong>
                  {reservation.evenement && reservation.evenement.titre
                    ? reservation.evenement.titre
                    : `Événement inconnu (${reservation.evenement?.id || "ID manquant"})`}
                </strong>
                <div>
                  <strong>Numéro de réservation :</strong> {reservation.id || "Non renseigné"}
                </div>
                <div>
                  <strong>Date de réservation :</strong>{" "}
                  {reservation.dateReservation
                    ? new Date(reservation.dateReservation).toLocaleString('fr-FR')
                    : "Date non renseignée"}
                </div>
                <div>
                  <strong>Date de l'événement :</strong>{" "}
                  {reservation.evenement && reservation.evenement.dateEvenement
                    ? new Date(reservation.evenement.dateEvenement).toLocaleDateString('fr-FR')
                    : "Date non renseignée"}
                </div>
                <div>
                  <strong>Billets :</strong> {reservation.nombreBillets > 0 ? reservation.nombreBillets : "Non renseigné"}
                </div>
                <div>
                  <strong>Prix unitaire :</strong>{" "}
                  {reservation.prixUnitaire ? formatPrice(reservation.prixUnitaire) : "Non renseigné"}
                </div>
                <div>
                  <strong>Prix total :</strong>{" "}
                  {reservation.prixTotal ? formatPrice(reservation.prixTotal) : "Non renseigné"}
                </div>
                <div>
                  <strong>Statut :</strong>{" "}
                  {reservation.statut === "CONFIRMEE" ? (
                    <span className="confirmed">Confirmée</span>
                  ) : (
                    <span className="not-confirmed">Non confirmée</span>
                  )}
                </div>
              </div>
              {reservation.statut === "CONFIRMEE" ? (
                <button onClick={() => handleVoirBillet(reservation.id)} className="btn-ticket">
                  Voir mon billet
                </button>
              ) : (
                <button onClick={() => handlePayment(reservation.id)} className="btn-pay">
                  Payer
                </button>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default MesReservations;
