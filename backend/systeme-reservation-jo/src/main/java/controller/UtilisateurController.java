package controller;

import model.Utilisateur;
import service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @GetMapping
    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurService.listerUtilisateurs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> trouverUtilisateurParId(@PathVariable Long id) {
        Utilisateur utilisateur = utilisateurService.trouverUtilisateurParId(id);
        // Si l'utilisateur est trouvé, on le renvoie avec un code 200 OK
        // Sinon, on renvoie un code 404 Not Found
        return utilisateur != null
                ? ResponseEntity.ok(utilisateur)
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Utilisateur> creerUtilisateur(@Valid @RequestBody Utilisateur utilisateur) {
        Utilisateur nouvelUtilisateur = utilisateurService.creerUtilisateur(utilisateur);
        // On renvoie le nouvel utilisateur créé avec un code 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelUtilisateur);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> modifierUtilisateur(@PathVariable Long id, @Valid @RequestBody Utilisateur utilisateur) {
        // TODO: Gérer la mise à jour de l'utilisateur avec l'ID spécifié
        // Pour le moment, on se contente de modifier l'utilisateur sans vérifier l'ID
        Utilisateur utilisateurModifie = utilisateurService.modifierUtilisateur(utilisateur);
        return utilisateurModifie != null
                ? ResponseEntity.ok(utilisateurModifie)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Long id) {
        utilisateurService.supprimerUtilisateur(id);
        // On renvoie un code 204 No Content après la suppression
        return ResponseEntity.noContent().build();
    }
}