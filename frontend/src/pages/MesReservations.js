// src/pages/MesReservations.js
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

    let userEmail = "";
    try {
      // Décodage du token pour récupérer l'email ou l'identifiant
      const payload = JSON.parse(atob(token.split('.')[1]));
      userEmail = payload.email || payload.sub;
      console.log("Email récupéré :", userEmail);
    } catch (error) {
      console.error("Erreur lors du décodage du token :", error);
      alert("Erreur d'authentification. Veuillez vous reconnecter.");
      navigate("/login");
      return;
    }

    // Appel API pour récupérer l'ensemble des réservations de l'utilisateur
    api.get(`/reservations/utilisateur?email=${encodeURIComponent(userEmail)}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(response => {
        const reservationsArray = Array.isArray(response.data) ? response.data : [];
        console.log("Nombre de réservations brutes reçues :", reservationsArray.length);

        // Pour chaque réservation, si "evenement" n'est pas un objet complet, récupérer l'ID depuis either reservation.evenement or reservation.evenement_id
        Promise.all(
          reservationsArray.map(async reservation => {
            // Récupère l'ID d'événement depuis reservation.evenement s'il s'agit d'un objet ou directement depuis evenement_id
            const eventId =
              (reservation.evenement && typeof reservation.evenement === "object"
                ? reservation.evenement.id
                : null) ||
              reservation.evenement ||
              reservation.evenement_id;

            if (!eventId) {
              console.warn("Aucun ID d'événement trouvé pour la réservation", reservation.id);
              return {
                ...reservation,
                evenement: {
                  id: "ID inconnu",
                  titre: "Événement non chargé correctement",
                  dateEvenement: "Date inconnue",
                  lieu: "Lieu indisponible"
                }
              };
            }

            // Si l'objet est déjà complet, on le laisse tel quel
            if (reservation.evenement && typeof reservation.evenement === 'object') {
              return reservation;
            }

            console.warn(`ID événement trouvé : ${eventId}. Récupération des détails...`);
            try {
              const eventResponse = await api.get(`/evenements/${eventId}`, {
                headers: { Authorization: `Bearer ${token}` }
              });
              return { ...reservation, evenement: eventResponse.data };
            } catch (error) {
              console.error("Erreur lors de la récupération de l'événement :", error);
              return {
                ...reservation,
                evenement: {
                  id: eventId,
                  titre: "Événement non chargé correctement",
                  dateEvenement: "Date inconnue",
                  lieu: "Lieu indisponible"
                }
              };
            }
          })
        )
          .then(updatedReservations => {
            console.log("Nombre de réservations après mise à jour :", updatedReservations.length);
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
          {reservations.map((reservation) => (
            <li key={reservation.id} className="reservation-item">
              <strong>
                {reservation.evenement && reservation.evenement.titre
                  ? reservation.evenement.titre
                  : `Événement inconnu (${reservation.evenement?.id || "ID manquant"})`}
              </strong>
              <div>
                {reservation.dateReservation 
                  ? new Date(reservation.dateReservation).toLocaleDateString('fr-FR')
                  : "Date non renseignée"}
              </div>
              <div>
                {reservation.nombreBillets > 0 
                  ? `Billets : ${reservation.nombreBillets}`
                  : "Billets non renseignés"}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default MesReservations;
