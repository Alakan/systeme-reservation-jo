package service;

import model.Billet;
import repository.BilletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BilletService {

    @Autowired
    private BilletRepository billetRepository;

    public Billet creerBillet(Billet billet) {
        // TODO: Ajouter la validation des données et la gestion des erreurs
        return billetRepository.save(billet);
    }

    public Billet modifierBillet(Billet billet) {
        // TODO: Ajouter la validation des données et la gestion des erreurs
        return billetRepository.save(billet);
    }

    public void supprimerBillet(Long id) {
        billetRepository.deleteById(id);
    }

    public Billet trouverBilletParId(Long id) {
        // TODO: Gérer le cas où le billet n'est pas trouvé
        return billetRepository.findById(id).orElse(null);
    }

    public List<Billet> listerBillets() {
        return billetRepository.findAll();
    }

    // TODO: Ajouter des méthodes pour rechercher des billets par événement, par type, etc.
}