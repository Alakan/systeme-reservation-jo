package service;

import model.Evenement;
import repository.EvenementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvenementService {

    @Autowired
    private EvenementRepository evenementRepository;

    public Evenement creerEvenement(Evenement evenement) {
        // TODO: Ajouter la validation des données et la gestion des erreurs
        return evenementRepository.save(evenement);
    }

    public Evenement modifierEvenement(Evenement evenement) {
        // TODO: Ajouter la validation des données et la gestion des erreurs
        return evenementRepository.save(evenement);
    }

    public void supprimerEvenement(Long id) {
        evenementRepository.deleteById(id);
    }

    public Evenement trouverEvenementParId(Long id) {
        // TODO: Gérer le cas où l'événement n'est pas trouvé
        return evenementRepository.findById(id).orElse(null);
    }

    public List<Evenement> listerEvenements() {
        return evenementRepository.findAll();
    }

    // TODO: Ajouter des méthodes pour rechercher des événements par nom, par date, etc.
}