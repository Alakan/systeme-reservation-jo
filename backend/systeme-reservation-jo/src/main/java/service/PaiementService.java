package service;

import model.Paiement;
import repository.PaiementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;

    public Paiement creerPaiement(Paiement paiement) {
        // TODO: Ajouter la validation des données, la gestion des erreurs et la logique de paiement
        return paiementRepository.save(paiement);
    }

    public Paiement modifierPaiement(Paiement paiement) {
        // TODO: Ajouter la validation des données et la gestion des erreurs
        return paiementRepository.save(paiement);
    }

    public void supprimerPaiement(Long id) {
        paiementRepository.deleteById(id);
    }

    public Paiement trouverPaiementParId(Long id) {
        // TODO: Gérer le cas où le paiement n'est pas trouvé
        return paiementRepository.findById(id).orElse(null);
    }

    public List<Paiement> listerPaiements() {
        return paiementRepository.findAll();
    }

    // TODO: Ajouter des méthodes pour rechercher des paiements par mode de paiement, par date, etc.
}