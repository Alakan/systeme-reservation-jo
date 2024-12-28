package service;

import model.Utilisateur;
import repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        // TODO: Ajouter la validation des données (email unique, mot de passe fort, etc.)
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur modifierUtilisateur(Utilisateur utilisateur) {
        // TODO: Ajouter la validation des données et la gestion des erreurs
        return utilisateurRepository.save(utilisateur);
    }

    public void supprimerUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }

    public Utilisateur trouverUtilisateurParId(Long id) {
        // TODO: Gérer le cas où l'utilisateur n'est pas trouvé
        return utilisateurRepository.findById(id).orElse(null);
    }

    public Utilisateur trouverUtilisateurParEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurRepository.findAll();
    }
}