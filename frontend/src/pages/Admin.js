import { useState, useEffect } from 'react';
import api from '../services/api';

function Admin() {
    const [offres, setOffres] = useState([]);

    useEffect(() => {
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

    const handleLogout = () => {
        localStorage.removeItem('token');
        window.location.href = "/login";
    };

    return (
        <div>
            <h1>Espace Administrateur</h1>
            <button onClick={handleLogout}>Déconnexion</button>
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
