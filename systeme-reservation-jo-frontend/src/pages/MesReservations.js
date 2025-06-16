import React, { useState, useEffect, useCallback } from 'react';
import { Link, useNavigate }                     from 'react-router-dom';
import api                                        from '../services/api';
import '../styles/MesReservations.css';

export default function MesReservations() {
  const [reservations, setReservations] = useState([]);
  const navigate                        = useNavigate();
  const token                           = localStorage.getItem('token');

  const formatPrice = (price) =>
    new Intl.NumberFormat('fr-FR', {
      style:    'currency',
      currency: 'EUR',
    }).format(price);

  const fetchReservations = useCallback(async () => {
    if (!token) {
      navigate('/login');
      return;
    }
    try {
      const { data } = await api.get('reservations/utilisateur', {
        headers: { Authorization: `Bearer ${token}` },
      });
      setReservations(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error('Erreur récupération réservations :', err);
      if (err.response?.status === 401) {
        alert('Session expirée, reconnectez-vous.');
        navigate('/login');
      } else {
        alert("Impossible de récupérer vos réservations.");
      }
    }
  }, [navigate, token]);

  useEffect(() => {
    fetchReservations();
  }, [fetchReservations]);

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
      alert('Mode de paiement invalide.');
      return;
    }
    try {
      await api.put(
        `reservations/${reservationId}/paiement`,
        { mode: choix },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      alert('Paiement réussi !');
      fetchReservations();
    } catch (err) {
      console.error('Erreur paiement :', err);
      alert('Échec du paiement.');
    }
  };

  const handleVoirBillet = (reservationId) =>
    navigate(`/billet/${reservationId}`);

  return (
    <div className="reservations-container">
      <h1>Mes Réservations</h1>

      <div className="actions">
        <Link to="/evenements" className="btn-back">
          ← Retour aux événements
        </Link>
      </div>

      {reservations.length === 0 ? (
        <p className="empty">Vous n'avez aucune réservation en cours.</p>
      ) : (
        <ul className="reservations-list">
          {reservations.map((r) => (
            <li key={r.id} className="reservation-item">
              <div className="reservation-details">
                <strong className="event-title">{r.evenement.titre}</strong>
                <div>
                  <span>Numéro :</span> {r.id}
                </div>
                <div>
                  <span>Date réservation :</span>{' '}
                  {new Date(r.dateReservation).toLocaleString('fr-FR')}
                </div>
                <div>
                  <span>Date événement :</span>{' '}
                  {new Date(r.evenement.dateEvenement).toLocaleDateString(
                    'fr-FR'
                  )}
                </div>
                <div>
                  <span>Billets :</span> {r.nombreBillets}
                </div>
                <div>
                  <span>Prix total :</span> {formatPrice(r.prixTotal)}
                </div>
                <div>
                  <span>Statut :</span>{' '}
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
