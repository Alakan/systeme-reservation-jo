// src/pages/MesReservations.js
import { useState, useEffect, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';
import '../styles/MesReservations.css';

function MesReservations() {
  const [reservations, setReservations] = useState([]);
  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  const formatPrice = (price) =>
    new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(price);

  // On crée fetchReservations avec useCallback pour le réutiliser et satisfaire ESLint
  const fetchReservations = useCallback(async () => {
    if (!token) {
      navigate('/login');
      return;
    }

    let userEmail;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      userEmail = payload.email || payload.sub;
    } catch (err) {
      console.error('Erreur décodage token :', err);
      alert("Erreur d'authentification. Veuillez vous reconnecter.");
      navigate('/login');
      return;
    }

    try {
      const { data } = await api.get(
        `reservations/utilisateur?email=${encodeURIComponent(userEmail)}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      // Filtrer et enrichir chaque réservation
      const valid = Array.isArray(data)
        ? data.filter((r) => r && r.id)
        : [];

      const updated = await Promise.all(
        valid.map(async (reservation) => {
          if (!reservation.evenement || typeof reservation.evenement !== 'object') {
            try {
              const ev = await api.get(`evenements/${reservation.evenement}`, {
                headers: { Authorization: `Bearer ${token}` }
              });
              return { ...reservation, evenement: ev.data };
            } catch {
              return {
                ...reservation,
                evenement: {
                  id: reservation.evenement || 'ID inconnu',
                  titre: 'Événement non chargé',
                  dateEvenement: 'Date inconnue',
                  lieu: 'Lieu indisponible'
                }
              };
            }
          }
          return reservation;
        })
      );

      setReservations(updated);
    } catch (err) {
      console.error('Erreur récupération réservations :', err);
      alert("Impossible de récupérer vos réservations.");
    }
  }, [navigate, token]);

  // on lance fetchReservations au montage
  useEffect(() => {
    fetchReservations();
  }, [fetchReservations]);

  // Paiement et rafraîchissement
  const handlePayment = async (reservationId) => {
    if (!token) {
      alert('Vous devez être connecté pour payer.');
      navigate('/login');
      return;
    }

    const choix = window
      .prompt('Mode de paiement : CARTE, PAYPAL, VIREMENT')
      ?.toUpperCase();
    if (!['CARTE', 'PAYPAL', 'VIREMENT'].includes(choix)) {
      alert('Mode invalide.');
      return;
    }

    try {
      await api.put(
        `reservations/${reservationId}/paiement`,
        JSON.stringify(choix),
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );
      alert('Paiement réussi !');
      await fetchReservations();
    } catch (err) {
      console.error('Erreur paiement :', err);
      alert('Échec du paiement.');
    }
  };

  const handleVoirBillet = (reservationId) => {
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
          {reservations.map((r) => (
            <li key={r.id} className="reservation-item">
              <div className="reservation-details">
                <strong>
                  {r.evenement?.titre || `Événement (${r.evenement?.id || 'ID'})`}
                </strong>
                <div>
                  <strong>Numéro :</strong> {r.id}
                </div>
                <div>
                  <strong>Date réservation :</strong>{' '}
                  {r.dateReservation
                    ? new Date(r.dateReservation).toLocaleString('fr-FR')
                    : 'N.C.'}
                </div>
                <div>
                  <strong>Date événement :</strong>{' '}
                  {r.evenement?.dateEvenement
                    ? new Date(r.evenement.dateEvenement).toLocaleDateString('fr-FR')
                    : 'N.C.'}
                </div>
                <div>
                  <strong>Billets :</strong>{' '}
                  {r.nombreBillets > 0 ? r.nombreBillets : 'N.C.'}
                </div>
                <div>
                  <strong>Prix total :</strong>{' '}
                  {r.prixTotal ? formatPrice(r.prixTotal) : 'N.C.'}
                </div>
                <div>
                  <strong>Statut :</strong>{' '}
                  {r.statut === 'CONFIRMEE' ? (
                    <span className="confirmed">Confirmée</span>
                  ) : (
                    <span className="not-confirmed">Non confirmée</span>
                  )}
                </div>
              </div>

              {r.statut === 'CONFIRMEE' ? (
                <button
                  onClick={() => handleVoirBillet(r.id)}
                  className="btn-ticket"
                >
                  Voir mon billet
                </button>
              ) : (
                <button
                  onClick={() => handlePayment(r.id)}
                  className="btn-pay"
                >
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
