// src/pages/Evenements.js
import React, { useState, useEffect } from 'react';
import { Link, useNavigate }           from 'react-router-dom';
import api                             from '../services/api';
import { useCart }                     from '../contexts/CartContext';
import '../styles/Evenements.css';

function Evenements() {
  const [evenements, setEvenements]       = useState([]);
  const [expandedDescriptions, setExpandedDescriptions] = useState({});
  const navigate                          = useNavigate();
  const { addToCart }                     = useCart();

  // Récupération des événements
  useEffect(() => {
    api.get('evenements')
      .then(response => {
        if (Array.isArray(response.data)) {
          setEvenements(response.data);
        } else {
          console.error('Réponse API inattendue :', response.data);
        }
      })
      .catch(err =>
        console.error('Erreur récupération événements :', err)
      );
  }, []);

  // Formatage de la date
  const formatDate = dateString =>
    new Date(dateString).toLocaleDateString('fr-FR', {
      year:   'numeric',
      month:  'long',
      day:    'numeric',
      hour:   '2-digit',
      minute: '2-digit'
    });

  // Formatage du prix
  const formatPrice = price =>
    new Intl.NumberFormat('fr-FR', {
      style:    'currency',
      currency: 'EUR'
    }).format(price);

  // Affiche/Masque la description complète
  const toggleDescription = id => {
    setExpandedDescriptions(prev => ({
      ...prev,
      [id]: !prev[id]
    }));
  };

  // Ajout au panier
  const handleAddToCart = evenement => {
    const token = localStorage.getItem('token');
    if (!token) {
      if (
        window.confirm(
          'Vous devez être connecté·e pour ajouter au panier. Continuer vers la connexion ?'
        )
      ) {
        navigate('/login');
      }
      return;
    }

    const qtyStr = window.prompt(
      `Combien de billets pour "${evenement.titre}" ?`
    );
    const qty = parseInt(qtyStr, 10);
    if (isNaN(qty) || qty <= 0) {
      return alert('Veuillez entrer un nombre de billets valide (> 0).');
    }

    addToCart(
      { id: evenement.id, name: evenement.titre, price: evenement.prix },
      qty
    );
    alert(`${qty} billet(s) ajouté(s) au panier.`);
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
              <div className="card h-100 border-0 shadow">
                <div className="card-body d-flex flex-column">
                  <h5 className="card-title text-primary">{ev.titre}</h5>
                  <h6 className="card-subtitle mb-3 text-muted">
                    {formatDate(ev.dateEvenement)}
                  </h6>
                  <p className="card-text mb-1">
                    <strong>Lieu:</strong> {ev.lieu}
                  </p>
                  <p className="card-text mb-3">
                    <strong>Prix:</strong> {formatPrice(ev.prix)}
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
                    className="btn btn-link p-0 mb-3 align-self-start"
                    onClick={() => toggleDescription(ev.id)}
                  >
                    {expandedDescriptions[ev.id] ? 'Voir moins' : 'Voir plus'}
                  </button>

                  <button
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
