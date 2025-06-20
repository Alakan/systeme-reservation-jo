// src/pages/CartPage.js
import React, { useState } from 'react';
import { useCart }          from '../contexts/CartContext';
import { useNavigate }      from 'react-router-dom';
import api                  from '../services/api';
import '../styles/CartPage.css';

function CartPage() {
  const { cart, removeFromCart, updateQuantity, clearCart } = useCart();
  const navigate     = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

  const totalPrice = cart.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );

  const handleCheckout = async () => {
    const token = localStorage.getItem('token');
    if (!token) {
      if (window.confirm('Vous devez être connecté·e… Voulez-vous vous connecter ?')) {
        return navigate('/login');
      }
      return;
    }

    // Décodage pour récupérer l'email
    let payload;
    try {
      payload = JSON.parse(atob(token.split('.')[1]));
    } catch {
      alert('Token invalide, reconnectez-vous.');
      return navigate('/login');
    }
    const userEmail = payload.sub;

    // Mode de paiement
    const modeInput = window.prompt('Mode de paiement : CARTE, PAYPAL ou VIREMENT');
    const mode      = modeInput ? modeInput.toUpperCase() : '';
    if (!['CARTE','PAYPAL','VIREMENT'].includes(mode)) {
      return alert('Mode de paiement invalide. Annulation.');
    }

    setIsLoading(true);

    try {
      // Pour chaque ligne du panier, on crée + paie
      for (const item of cart) {
        // 1️⃣ Création
        const res = await api.post(
          'reservations',
          {
            utilisateur:     { email: userEmail },
            evenement:       { id: item.id },
            dateReservation: new Date().toISOString(),
            nombreBillets:   item.quantity
          },
          {
            headers: {
              Authorization: 'Bearer ' + token,
              'Content-Type': 'application/json'
            }
          }
        );
        const reservationId = res.data.id;

        // 2️⃣ Paiement -> mettra à jour le statut en confirmed
        await api.put(
          `reservations/${reservationId}/paiement`,
          JSON.stringify(mode),
          {
            headers: {
              Authorization: 'Bearer ' + token,
              'Content-Type':  'application/json'
            }
          }
        );
      }

      alert('Réservation(s) confirmée(s) !');      
      clearCart();
      navigate('/mes-reservations');
    } catch (err) {
      console.error('Erreur checkout :', err);
      alert('Erreur lors de la réservation ou du paiement.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="cart-page">
      <h2>Votre Panier</h2>
      {cart.length === 0 ? (
        <p>Votre panier est vide.</p>
      ) : (
        <>
          <ul className="cart-list">
            {cart.map(item => (
              <li key={item.id} className="cart-item">
                <div className="item-info">
                  <span>{item.name}</span>
                  <span>{item.price.toFixed(2)} €</span>
                </div>
                <div className="item-controls">
                  <input
                    type="number"
                    min="1"
                    value={item.quantity}
                    onChange={e =>
                      updateQuantity(item.id, Number(e.target.value))
                    }
                  />
                  <button onClick={() => removeFromCart(item.id)}>
                    Supprimer
                  </button>
                </div>
              </li>
            ))}
          </ul>
          <div className="cart-summary">
            <h3>Total : {totalPrice.toFixed(2)} €</h3>
            <button onClick={handleCheckout} disabled={isLoading}>
              {isLoading ? 'Traitement…' : 'Valider la commande'}
            </button>
          </div>
        </>
      )}
    </div>
  );
}

export default CartPage;
