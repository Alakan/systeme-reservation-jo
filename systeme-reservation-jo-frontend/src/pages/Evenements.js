// src/pages/Evenements.js
import React, { useState, useEffect } from 'react';
import { Link, useNavigate }           from 'react-router-dom';
import api                             from '../services/api';
import { useCart }                     from '../contexts/CartContext';
import '../styles/Evenements.css';

function Evenements() {
  const [evenements, setEvenements]             = useState([]);
  const [expandedDescriptions, setExpandedDescriptions] = useState({});
  const navigate                                = useNavigate();
  const { addToCart }                           = useCart();

  // 1️⃣ Récupération des événements
  useEffect(() => {
    api.get('evenements')
      .then(res => {
        if (Array.isArray(res.data)) {
          setEvenements(res.data);
        } else {
          console.error('Réponse API inattendue :', res.data);
        }
      })
      .catch(err => console.error('Erreur fetch événements :', err));
  }, []);

  // 2️⃣ Helpers : formatage date & prix
  const formatDate = ds =>
    new Date(ds).toLocaleDateString('fr-FR', {
      year:   'numeric',
      month:  'long',
      day:    'numeric',
      hour:   '2-digit',
      minute: '2-digit'
    });

  const formatPrice = p =>
    new Intl.NumberFormat('fr-FR', {
      style:    'currency',
      currency: 'EUR'
    }).format(p);

  // 3️⃣ Toggle description complète / résumé
  const toggleDescription = id =>
    setExpandedDescriptions(prev => ({
      ...prev,
      [id]: !prev[id]
    }));

  // 4️⃣ Ajout au panier avec quantité
  const handleAddToCart = ev => {
    const token = localStorage.getItem('token');
    if (!token) {
      if (
        window.confirm(
          'Vous devez être connecté·e pour ajouter au panier. Se connecter maintenant ?'
        )
      ) {
        navigate('/login');
      }
      return;
    }

    const qtyStr = window.prompt(`Combien de billets pour “${ev.titre}” ?`);
    const qty    = parseInt(qtyStr, 10);
    if (isNaN(qty) || qty <= 0) {
      return alert('Veuillez entrer un nombre entier > 0.');
    }

    addToCart(
      { id: ev.id, name: ev.titre, price: ev.prix },
      qty
    );
    alert(`${qty} billet${qty > 1 ? 's' : ''} ajouté${qty > 1 ? 's' : ''} au panier.`);
  };

  return (
    <div className="container py-5">
      <h1 className="mb-5 text-center">Événements JO 2024</h1>

      <div className="text-center mb-4">
        <Link to="/">
          <button className="btn btn-outline-secondary">
            Retour à l’accueil
          </button>
        </Link>
      </div>

      {evenements.length === 0 ? (
        <p className="text-center">Aucun événement disponible.</p>
      ) : (
        <div className="row">
          {evenements.map(ev => (
            <div key={ev.id} className="col-lg-4 col-md-6 mb-4">
              <div className="card h-100 shadow-sm">
                <div className="card-body d-flex flex-column">
                  <h5 className="card-title text-primary">{ev.titre}</h5>
                  <h6 className="card-subtitle mb-3 text-muted">
                    {formatDate(ev.dateEvenement)}
                  </h6>
                  <p className="card-text mb-1">
                    <strong>Lieu :</strong> {ev.lieu}
                  </p>
                  <p className="card-text mb-3">
                    <strong>Prix :</strong> {formatPrice(ev.prix)}
                  </p>

                  {expandedDescriptions[ev.id] ? (
                    <p className="card-text">{ev.description}</p>
                  ) : (
                    <p className="card-text">
                      {ev.description.length > 100
                        ? ev.description.slice(0, 100) + '…'
                        : ev.description}
                    </p>
                  )}

                  <button
                    type="button"
                    className="btn btn-link p-0 mb-3 align-self-start"
                    onClick={() => toggleDescription(ev.id)}
                  >
                    {expandedDescriptions[ev.id] ? 'Voir moins' : 'Voir plus'}
                  </button>

                  <button
                    type="button"
                    className="btn btn-success mt-auto"
                    onClick={() => handleAddToCart(ev)}
                  >
                    Ajouter au panier
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Evenements;
