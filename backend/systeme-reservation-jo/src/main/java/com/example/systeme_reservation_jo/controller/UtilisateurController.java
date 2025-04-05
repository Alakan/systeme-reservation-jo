package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    // Récupération de tous les utilisateurs (avec possibilité future de paginer les résultats)
    @GetMapping
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();
        return ResponseEntity.ok(utilisateurs);
    }

    // Récupération d'un utilisateur par ID avec gestion des erreurs
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Integer id) {
        Optional<Utilisateur> utilisateur = utilisateurService.getUtilisateurById(id);
        if (utilisateur.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Pas trouvé
        }
        return ResponseEntity.ok(utilisateur.get());
    }

    // Création d'un nouvel utilisateur avec vérification de l'existence de l'email
    @PostMapping
    public ResponseEntity<?> saveUtilisateur(@Valid @RequestBody Utilisateur utilisateur) {
        if (utilisateurService.existsByEmail(utilisateur.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("message", "L'email est déjà utilisé !"));
        }
        Utilisateur nouvelUtilisateur = utilisateurService.saveUtilisateur(utilisateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelUtilisateur);
    }

    // Mise à jour des informations d'un utilisateur avec gestion des erreurs
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUtilisateur(@PathVariable Integer id, @Valid @RequestBody Utilisateur utilisateurDetails) {
        Optional<Utilisateur> utilisateurOptional = utilisateurService.getUtilisateurById(id);
        if (utilisateurOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Utilisateur non trouvé"));
        }
        Utilisateur utilisateurModifie = utilisateurService.updateUtilisateur(id, utilisateurDetails);
        return ResponseEntity.ok(utilisateurModifie);
    }

    // Suppression d'un utilisateur par ID avec gestion des erreurs
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUtilisateur(@PathVariable Integer id) {
        Optional<Utilisateur> utilisateurOptional = utilisateurService.getUtilisateurById(id);
        if (utilisateurOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Utilisateur non trouvé"));
        }
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint protégé accessible uniquement aux administrateurs
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMINISTRATEUR')") // Protection basée sur le rôle ADMINISTRATEUR
    public ResponseEntity<String> getAdminResource() {
        return ResponseEntity.ok("Bienvenue, Administrateur !");
    }
}
