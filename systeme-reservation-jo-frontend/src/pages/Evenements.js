// src/pages/Evenements.js
import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';
import '../styles/Evenements.css';

function Evenements() {
  const [evenements, setEvenements] = useState([]);
  const navigate = useNavigate();

  // Récupération des événements accessibles à tous (visiteurs)
  useEffect(() => {
    // Utilisation d'un chemin relatif sans slash initial
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

  // Formatage du prix en euro (avec deux décimales)
  const formatPrice = (price) => {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR'
    }).format(price);
  };

  const handleReservation = async (evenement) => {
    const token = localStorage.getItem("token");
    if (!token) {
      if (window.confirm("Vous devez être connecté pour réserver. Voulez-vous vous inscrire maintenant ?")) {
        navigate("/register");
      }
      return;
    }

    // Décodage du token pour récupérer l'email de l'utilisateur
    let payload;
    try {
      payload = JSON.parse(atob(token.split('.')[1]));
    } catch (error) {
      console.error("Erreur lors du décodage du token :", error);
      alert("Token invalide, veuillez vous reconnecter.");
      navigate("/login");
      return;
    }
    const userEmail = payload.sub; // On considère ici que "sub" contient l'email

    // Demande du nombre de billets souhaités
    const billetsStr = window.prompt("Combien de billets souhaitez-vous réserver ?");
    const nombreBillets = parseInt(billetsStr, 10);
    if (isNaN(nombreBillets) || nombreBillets <= 0) {
      alert("Saisie invalide. Le nombre de billets doit être un nombre supérieur à 0.");
      return;
    }

    // Calcul du montant total de la réservation
    const totalPrice = evenement.prix * nombreBillets;

    // Afficher le montant total et demander confirmation
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

    // Création de la réservation avec les informations validées
    try {
      // Utilisation d'un chemin relatif pour la réservation
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

    // Utilisation d'un chemin relatif pour le paiement
    api.put(
      `reservations/${reservationId}/paiement`,
      JSON.stringify(modePaiement),
      { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
    )
    .then(() => {
      alert("Paiement effectué avec succès !");
      navigate("/mes-reservations"); // Redirection vers la page MesReservations
    })
    .catch(error => {
      console.error("Erreur lors du paiement :", error);
      alert("Erreur lors du paiement.");
    });
  };

  return (
    <div className="evenements-container">
      <h1>Événements disponibles</h1>
      <Link to="/"><button className="btn-home">Retour à la page principale</button></Link>

      {evenements.length === 0 ? (
        <p>Aucun événement disponible.</p>
      ) : (
        <ul className="evenements-list">
          {evenements.map((evenement) => (
            <li key={evenement.id} className="evenement-item">
              <div>
                <strong>{evenement.titre}</strong>
                <br />
                {formatDate(evenement.dateEvenement)}
                <br />
                {evenement.lieu}
                <br />
                <span>Prix : {formatPrice(evenement.prix)}</span>
              </div>
              <div className="evenement-actions">
                <button onClick={() => handleReservation(evenement)}>Réserver</button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default Evenements;
