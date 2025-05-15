import { useState, useEffect } from 'react';
import api from '../services/api';

function Evenements() {
    const [evenements, setEvenements] = useState([]);

    useEffect(() => {
        // Appel API pour récupérer la liste des événements
        api.get('/evenements')
            .then(response => setEvenements(response.data))
            .catch(error => console.error("Erreur lors de la récupération des événements :", error));
    }, []);

    return (
        <div>
            <h1>Événements disponibles</h1>
            <ul>
                {evenements.map(evenement => (
                    <li key={evenement.id}>
                        <strong>{evenement.titre}</strong> - {evenement.date} - {evenement.lieu}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default Evenements;
