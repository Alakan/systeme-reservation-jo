import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';
import '../styles/Evenements.css';

function Evenements() {
  const [evenements, setEvenements] = useState([]);
  const navigate = useNavigate();

  // Récupération des événements accessible à tous (visiteurs)
  useEffect(() => {
    api.get('/evenements')
      .then(response => {
        if (Array.isArray(response.data)) {
          setEvenements(response.data);
        } else {
          console.error("Réponse API inattendue :", response.data);
        }
      })
      .catch(error => console.error("Erreur lors de la récupération des événements :", error));
  }, []);

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      year: 'numeric', month: 'long', day: 'numeric', 
      hour: '2-digit', minute: '2-digit' 
    });
  };

  const handleReservation = async (evenement) => {
    const token = localStorage.getItem("token");

    if (!token) {
      // Propose à l'utilisateur de s'inscrire s'il n'est pas connecté
      if (window.confirm("Vous devez être connecté pour réserver. Voulez-vous vous inscrire maintenant ?")) {
        navigate("/register");
      }
      return;
    }

    // Extraction d'informations utilisateurs à partir du token (attention : ce n'est qu'un exemple)
    const payload = JSON.parse(atob(token.split('.')[1]));
    const userEmail = payload.sub;

    try {
      const response = await api.post('/reservations', { 
        utilisateur: { email: userEmail },
        evenement, 
        dateReservation: new Date().toISOString(), 
        nombreBillets: 1 
      }, { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } });

      const reservationId = response.data.id;
      console.log("Réservation créée avec ID :", reservationId);

      // Demande du mode de paiement via prompt
      const modePaiement = window.prompt("Choisissez votre mode de paiement : CARTE, PAYPAL, VIREMENT")?.toUpperCase();

      if (modePaiement && ["CARTE", "PAYPAL", "VIREMENT"].includes(modePaiement)) {
        handlePaiement(reservationId, modePaiement);
      } else {
        alert("Mode de paiement invalide. Veuillez réessayer.");
      }

      alert(`Réservation effectuée pour ${evenement.titre} !`);
    } catch (error) {
      console.error("Erreur lors de la réservation :", error);
    }
  };

  const handlePaiement = (reservationId, modePaiement) => {
    console.log("Paiement en cours pour réservation ID :", reservationId, "Mode :", modePaiement);
    const token = localStorage.getItem('token');
    if (!token) {
      alert("Erreur : vous devez être connecté.");
      return;
    }

    api.put(`/reservations/${reservationId}/paiement`, JSON.stringify(modePaiement), {
      headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
    })
    .then(() => {
      alert("Paiement effectué avec succès !");
      navigate("/reservations"); // Redirection (pour la suite, la page des réservations devra être créée)
    })
    .catch(error => console.error("Erreur lors du paiement :", error));
  };

  // Gestion des favoris : stocke l'événement dans localStorage
  const handleFavori = (evenement) => {
    let favoris = JSON.parse(localStorage.getItem("favoris")) || [];
    favoris.push(evenement);
    localStorage.setItem("favoris", JSON.stringify(favoris));
    alert("Événement ajouté aux favoris !");
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
              </div>
              <div className="evenement-actions">
                <button onClick={() => handleReservation(evenement)}>Réserver</button>
                <button onClick={() => handleFavori(evenement)}>Favori</button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default Evenements;
