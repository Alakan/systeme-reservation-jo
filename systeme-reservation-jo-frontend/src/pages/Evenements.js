// src/pages/Evenements.js
import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';
import '../styles/Evenements.css';

function Evenements() {
  const [evenements, setEvenements] = useState([]);
  const [expandedDescriptions, setExpandedDescriptions] = useState({});
  const navigate = useNavigate();

  // Récupération des événements accessibles à tous (visiteurs)
  useEffect(() => {
    api.get("evenements")
      .then(response => {
        if (Array.isArray(response.data)) {
          setEvenements(response.data);
        } else {
          console.error("Réponse API inattendue :", response.data);
        }
      })
      .catch(error => console.error("Erreur lors de la récupération des événements :", error));
  }, []);

  // Formatage de la date
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      year: 'numeric', 
      month: 'long', 
      day: 'numeric', 
      hour: '2-digit', 
      minute: '2-digit'
    });
  };

  // Formatage du prix en euro
  const formatPrice = (price) => {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(price);
  };

  // Bascule de l'affichage complet/reduit de la description d'un événement
  const toggleDescription = (id) => {
    setExpandedDescriptions(prev => ({
      ...prev,
      [id]: !prev[id]
    }));
  };

  const handleReservation = async (evenement) => {
    const token = localStorage.getItem("token");
    if (!token) {
      if (window.confirm("Vous devez être connecté pour réserver. Voulez-vous vous inscrire maintenant ?")) {
        navigate("/register");
      }
      return;
    }

    // Décodage du token pour récupérer l'email
    let payload;
    try {
      payload = JSON.parse(atob(token.split('.')[1]));
    } catch (error) {
      console.error("Erreur lors du décodage du token :", error);
      alert("Token invalide, veuillez vous reconnecter.");
      navigate("/login");
      return;
    }
    const userEmail = payload.sub; // "sub" contient l'email

    // Demande du nombre de billets
    const billetsStr = window.prompt("Combien de billets souhaitez-vous réserver ?");
    const nombreBillets = parseInt(billetsStr, 10);
    if (isNaN(nombreBillets) || nombreBillets <= 0) {
      alert("Saisie invalide. Le nombre de billets doit être un nombre supérieur à 0.");
      return;
    }

    // Calcul du montant total
    const totalPrice = evenement.prix * nombreBillets;
    if (!window.confirm(`Le montant total de votre réservation est de ${formatPrice(totalPrice)}. Voulez-vous continuer ?`)) {
      return;
    }

    // Demande du mode de paiement
    const modePaiementInput = window.prompt("Choisissez votre mode de paiement : CARTE, PAYPAL, VIREMENT");
    const modePaiement = modePaiementInput ? modePaiementInput.toUpperCase() : "";
    if (!["CARTE", "PAYPAL", "VIREMENT"].includes(modePaiement)) {
      alert("Mode de paiement invalide. La réservation est annulée.");
      return;
    }

    try {
      // Création de la réservation
      const response = await api.post(
        "reservations",
        { 
          utilisateur: { email: userEmail },
          evenement, 
          dateReservation: new Date().toISOString(), 
          nombreBillets: nombreBillets
        },
        { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
      );

      const reservationId = response.data.id;
      console.log("Réservation créée avec ID :", reservationId);

      // Procéder au paiement
      await handlePaiement(reservationId, modePaiement);

      alert(`Réservation effectuée pour ${evenement.titre} avec ${nombreBillets} billet(s) !`);
    } catch (error) {
      console.error("Erreur lors de la réservation :", error);
      alert("Erreur lors de la réservation.");
    }
  };

  const handlePaiement = (reservationId, modePaiement) => {
    const token = localStorage.getItem('token');
    if (!token) {
      alert("Erreur : vous devez être connecté.");
      return;
    }

    api.put(
      `reservations/${reservationId}/paiement`,
      JSON.stringify(modePaiement),
      { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
    )
    .then(() => {
      alert("Paiement effectué avec succès !");
      navigate("/mes-reservations");
    })
    .catch(error => {
      console.error("Erreur lors du paiement :", error);
      alert("Erreur lors du paiement.");
    });
  };

  return (
    <div className="container py-4">
      <h1 className="mb-4 text-center">Événements JO 2024</h1>
      <div className="mb-3 text-center">
        <Link to="/">
          <button className="btn btn-secondary">Retour à la page principale</button>
        </Link>
      </div>

      {evenements.length === 0 ? (
        <p className="text-center">Aucun événement disponible.</p>
      ) : (
        <div className="row">
          {evenements.map(evenement => (
            <div key={evenement.id} className="col-md-4 col-sm-6 mb-4">
              <div className="card h-100 shadow-sm">
                <div className="card-body d-flex flex-column">
                  <h5 className="card-title">{evenement.titre}</h5>
                  <h6 className="card-subtitle mb-2 text-muted">{formatDate(evenement.dateEvenement)}</h6>
                  <p className="card-text">{evenement.lieu}</p>
                  <p className="card-text">Prix : {formatPrice(evenement.prix)}</p>
                  {expandedDescriptions[evenement.id] ? (
                    <p className="card-text">{evenement.description}</p>
                  ) : (
                    <p className="card-text">
                      {evenement.description.length > 100 
                        ? evenement.description.slice(0, 100) + '...' 
                        : evenement.description}
                    </p>
                  )}
                  <button 
                    className="btn btn-link p-0 align-self-start mb-3" 
                    onClick={() => toggleDescription(evenement.id)}
                  >
                    {expandedDescriptions[evenement.id] ? 'Voir moins' : 'Voir plus'}
                  </button>
                  <div className="mt-auto">
                    <button 
                      className="btn btn-primary w-100" 
                      onClick={() => handleReservation(evenement)}
                    >
                      Réserver
                    </button>
                  </div>
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
