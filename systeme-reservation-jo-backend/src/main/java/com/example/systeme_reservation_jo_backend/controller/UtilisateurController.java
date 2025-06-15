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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Optional<Utilisateur> opt = utilisateurService.findByEmail(email);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur non trouvé.");
        }
        Utilisateur user = opt.get();
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        // Mot de passe volontairement non exposé
        return ResponseEntity.ok(dto);
    }

    /**
     * Permet à l'utilisateur connecté de mettre à jour son profil.
     * L'utilisateur peut changer email/username, et *optionnellement* son mot de passe.
     * Si newPassword est fourni, currentPassword devient obligatoire.
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            @Valid @RequestBody UpdateProfileDTO dto) {

        // Récupération de l'utilisateur depuis le token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Optional<Utilisateur> opt = utilisateurService.findByEmail(email);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur non trouvé.");
        }
        Utilisateur user = opt.get();

        // 1) Mise à jour email & username
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());

        // 2) Si l'on veut changer le mot de passe...
        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {

            // 2a) L'ancien mot de passe doit être fourni
            if (dto.getCurrentPassword() == null || dto.getCurrentPassword().isBlank()) {
                return ResponseEntity.badRequest()
                        .body("Vous devez fournir votre mot de passe actuel.");
            }
            // 2b) Vérification de l'ancien mot de passe
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Mot de passe actuel incorrect.");
            }
            // 2c) On encode et on assigne le nouveau mot de passe
            user.setPassword(dto.getNewPassword());
        }
        // Sinon, si newPassword est vide, on ne touche pas au password

        // 3) Sauvegarde via le service (il gère l'encodage si nécessaire)
        Utilisateur updated = utilisateurService.updateUtilisateur(user.getId(), user);

        // 4) Préparation de la réponse
        UtilisateurDTO out = new UtilisateurDTO();
        out.setId(updated.getId());
        out.setEmail(updated.getEmail());
        out.setUsername(updated.getUsername());
        // pas de password exposé
        return ResponseEntity.ok(out);
    }

    /**
     * Récupère un utilisateur via son identifiant.
     * Utile pour pré-remplir les formulaires d'administration.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUtilisateurById(@PathVariable Long id) {
        Optional<Utilisateur> opt = utilisateurService.getUtilisateurById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur non trouvé.");
        }
        Utilisateur user = opt.get();
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        // Mot de passe volontairement non exposé
        return ResponseEntity.ok(dto);
    }
}
