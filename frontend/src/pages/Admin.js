import { useState, useEffect } from 'react';
import api from '../services/api';

function Admin() {
    const [offres, setOffres] = useState([]);

    useEffect(() => {
        // Récupérer les offres existantes
        api.get('/admin/offres')
            .then(response => setOffres(response.data))
            .catch(error => console.error("Erreur lors de la récupération des offres :", error));
    }, []);

    const handleDelete = (id) => {
        api.delete(`/admin/offres/${id}`)
            .then(() => {
                setOffres(offres.filter(offre => offre.id !== id));
                alert("Offre supprimée avec succès.");
            })
            .catch(error => console.error("Erreur lors de la suppression :", error));
    };

    return (
        <div>
            <h1>Espace Administrateur</h1>
            <ul>
                {offres.map(offre => (
                    <li key={offre.id}>
                        {offre.nom} - {offre.prix}€
                        <button onClick={() => handleDelete(offre.id)}>Supprimer</button>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default Admin;
