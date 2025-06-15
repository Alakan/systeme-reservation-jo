package com.example.systeme_reservation_jo_backend.controller;

import com.example.systeme_reservation_jo_backend.dto.UpdateProfileDTO;
import com.example.systeme_reservation_jo_backend.dto.UtilisateurDTO;
import com.example.systeme_reservation_jo_backend.model.Utilisateur;
import com.example.systeme_reservation_jo_backend.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://front-systeme-reservation-jo-be1e62ad3714.herokuapp.com"
})
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UtilisateurController(UtilisateurService utilisateurService,
                                 PasswordEncoder passwordEncoder) {
        this.utilisateurService = utilisateurService;
        this.passwordEncoder   = passwordEncoder;
    }

    /**
     * Récupère le profil de l'utilisateur connecté.
     * Utilise l'email contenu dans le token JWT pour chercher l'utilisateur.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        // Récupération de l'email de l'utilisateur depuis le contexte de sécurité
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);
        if (utilisateurOpt.isPresent()) {
            Utilisateur user = utilisateurOpt.get();
            UtilisateurDTO dto = new UtilisateurDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
            // On n'expose pas le mot de passe pour des raisons de sécurité
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur non trouvé.");
        }
    }

    /**
     * Permet à l'utilisateur connecté de mettre à jour son profil.
     * L'utilisateur peut modifier son email, son nom d'utilisateur et
     * changer son mot de passe à condition de fournir l'ancien.
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            @Valid @RequestBody UpdateProfileDTO dto) {
        // Récupération de l'utilisateur connecté depuis le token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);
        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur non trouvé.");
        }
        Utilisateur user = utilisateurOpt.get();

        // Mise à jour des informations autorisées
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());

        // Si un nouveau mot de passe est fourni, on le vérifie et on le met à jour
        if (dto.getNewPassword() != null && !dto.getNewPassword().trim().isEmpty()) {
            // L'ancien mot de passe doit être présent
            if (dto.getCurrentPassword() == null || dto.getCurrentPassword().trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body("Vous devez fournir votre mot de passe actuel pour le changer.");
            }
            // Vérification de l'ancien mot de passe
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Mot de passe actuel incorrect.");
            }
            // On encode et on assigne le nouveau mot de passe
            user.setPassword(dto.getNewPassword());
        }

        // Mise à jour de l'utilisateur grâce au service
        Utilisateur updatedUser = utilisateurService.updateUtilisateur(user.getId(), user);

        // Conversion en DTO pour la réponse
        UtilisateurDTO updatedDTO = new UtilisateurDTO();
        updatedDTO.setId(updatedUser.getId());
        updatedDTO.setEmail(updatedUser.getEmail());
        updatedDTO.setUsername(updatedUser.getUsername());
        // On n'expose pas le mot de passe
        return ResponseEntity.ok(updatedDTO);
    }

    /**
     * Récupère un utilisateur via son identifiant.
     * Cet endpoint est utile, par exemple, pour pré-remplir
     * les formulaires d'administration.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUtilisateurById(@PathVariable Long id) {
        Optional<Utilisateur> utilisateurOpt = utilisateurService.getUtilisateurById(id);
        if (utilisateurOpt.isPresent()) {
            Utilisateur user = utilisateurOpt.get();
            UtilisateurDTO dto = new UtilisateurDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
            // On évite d'exposer le mot de passe pour des raisons de sécurité
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur non trouvé.");
        }
    }
}
