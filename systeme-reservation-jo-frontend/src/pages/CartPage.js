import React, { useState } from 'react';
import { useCart }          from '../contexts/CartContext';
import { useNavigate }      from 'react-router-dom';
import api                  from '../services/api';
import '../styles/CartPage.css';

function CartPage() {
  const { cart, removeFromCart, updateQuantity, clearCart } = useCart();
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

  const totalPrice = cart.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );

  const handleCheckout = async () => {
    const token = localStorage.getItem('token');
    if (!token) {
      if (
        window.confirm(
          "Vous devez être connecté·e pour passer commande. Voulez-vous vous connecter ?"
        )
      ) {
        navigate('/login');
      }
      return;
    }

    // Décodage du token pour récupérer l'email
    let payload;
    try {
      payload = JSON.parse(atob(token.split('.')[1]));
    } catch {
      alert('Token invalide, veuillez vous reconnecter.');
      return navigate('/login');
    }
    const userEmail = payload.sub;

    // Saisie du mode de paiement
    const modeInput = window.prompt(
      'Mode de paiement : CARTE, PAYPAL ou VIREMENT'
    );
    const mode = modeInput?.toUpperCase() || '';
    if (!['CARTE', 'PAYPAL', 'VIREMENT'].includes(mode)) {
      return alert('Mode de paiement invalide. Annulation.');
    }

    setIsLoading(true);
    try {
      // Création d'une réservation par item dans le panier
      for (const item of cart) {
        await api.post(
          'reservations',
          {
            utilisateur: { email: userEmail },
            evenement:   { id: item.id },
            dateReservation: new Date().toISOString(),
            nombreBillets:   item.quantity,
            modePaiement:     mode
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          }
        );
      }

      alert('Réservation(s) créée(s) avec succès !');
      clearCart();
      navigate('/mes-reservations');
    } catch (err) {
      console.error('Checkout error:', err);
      alert("Erreur lors de la création de votre réservation.");
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
```