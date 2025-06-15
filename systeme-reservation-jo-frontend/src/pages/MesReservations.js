// src/pages/MesReservations.js
import { useState, useEffect, useCallback } from 'react'
import { Link, useNavigate }          from 'react-router-dom'
import api                            from '../services/api'
import '../styles/MesReservations.css'

function MesReservations() {
  const [reservations, setReservations] = useState([])
  const navigate                        = useNavigate()
  const token                           = localStorage.getItem('token')

  const formatPrice = (price) =>
    new Intl.NumberFormat('fr-FR', {
      style:    'currency',
      currency: 'EUR',
    }).format(price)

  const fetchReservations = useCallback(async () => {
    if (!token) {
      navigate('/login')
      return
    }

    try {
      // 1) On interroge directement l’endpoint “/api/reservations/utilisateur”
      //    qui renvoie les réservations liées au user issu du JWT
      const { data } = await api.get('reservations/utilisateur', {
        headers: { Authorization: `Bearer ${token}` },
      })

      // 2) on s’assure que c’est un tableau
      setReservations(Array.isArray(data) ? data : [])
    } catch (err) {
      console.error('Erreur récupération réservations :', err)
      // 401 on force la reco si le token est expiré
      if (err.response?.status === 401) {
        alert('Votre session a expiré, veuillez vous reconnecter.')
        navigate('/login')
      } else {
        alert("Impossible de récupérer vos réservations.")
      }
    }
  }, [navigate, token])

  useEffect(() => {
    fetchReservations()
  }, [fetchReservations])

  const handlePayment = async (reservationId) => {
    if (!token) {
      alert('Vous devez être connecté pour payer.')
      navigate('/login')
      return
    }

    const choix = window.prompt('Mode de paiement : CARTE, PAYPAL, VIREMENT')
      ?.toUpperCase()
    if (!['CARTE', 'PAYPAL', 'VIREMENT'].includes(choix)) {
      alert('Mode de paiement invalide.')
      return
    }

    try {
      await api.put(
        `reservations/${reservationId}/paiement`,
        { mode: choix },
        { headers: { Authorization: `Bearer ${token}` } }
      )
      alert('Paiement réussi !')
      fetchReservations()
    } catch (err) {
      console.error('Erreur paiement :', err)
      alert('Échec du paiement.')
    }
  }

  const handleVoirBillet = (reservationId) =>
    navigate(`/billet/${reservationId}`)

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
                <strong>{r.evenement.titre}</strong>
                <div>
                  <strong>Numéro :</strong> {r.id}
                </div>
                <div>
                  <strong>Date réservation :</strong>{' '}
                  {new Date(r.dateReservation).toLocaleString('fr-FR')}
                </div>
                <div>
                  <strong>Date événement :</strong>{' '}
                  {new Date(r.evenement.dateEvenement).toLocaleDateString(
                    'fr-FR'
                  )}
                </div>
                <div>
                  <strong>Billets :</strong> {r.nombreBillets}
                </div>
                <div>
                  <strong>Prix total :</strong>{' '}
                  {formatPrice(r.prixTotal)}
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
  )
}

export default MesReservations
