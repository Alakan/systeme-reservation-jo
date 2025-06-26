// src/main/java/com/example/systeme_reservation_jo_backend/service/UtilisateurServiceImpl.java
package com.example.systeme_reservation_jo_backend.service;

import com.example.systeme_reservation_jo_backend.model.Utilisateur;
import com.example.systeme_reservation_jo_backend.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder      passwordEncoder;

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository,
                                  PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder       = passwordEncoder;
    }

    // Retourne la liste complète des utilisateurs
    @Override
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    // Retourne uniquement les utilisateurs ayant le rôle administrateur
    @Override
    public List<Utilisateur> getAllAdminUsers() {
        return utilisateurRepository.findByRoles_Name("ROLE_ADMINISTRATEUR");
    }

    // Vérifie si un utilisateur existe par email
    @Override
    public boolean existsByEmail(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    // Recherche un utilisateur par son email
    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    // Sauvegarde un nouvel utilisateur avec encodage du mot de passe si nécessaire
    @Override
    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        if (utilisateur.getPassword() != null
                && !utilisateur.getPassword().isBlank()
                && !utilisateur.getPassword().startsWith("$2a$")) {
            utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        }
        return utilisateurRepository.save(utilisateur);
    }

    // Mise à jour des informations d'un utilisateur existant identifié par son id
    @Override
    public Utilisateur updateUtilisateur(Long id, Utilisateur utilisateurDetails) {
        Optional<Utilisateur> opt = utilisateurRepository.findById(id);
        if (opt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        Utilisateur utilisateur = opt.get();
        utilisateur.setUsername(utilisateurDetails.getUsername());
        utilisateur.setEmail(utilisateurDetails.getEmail());

        if (utilisateurDetails.getPassword() != null && !utilisateurDetails.getPassword().isBlank()) {
            if (!utilisateurDetails.getPassword().startsWith("$2a$")) {
                utilisateur.setPassword(passwordEncoder.encode(utilisateurDetails.getPassword()));
            } else {
                utilisateur.setPassword(utilisateurDetails.getPassword());
            }
        }

        return utilisateurRepository.save(utilisateur);
    }

    // Supprime un utilisateur : on purge d'abord la jointure utilisateur_roles, puis on supprime la ligne
    @Override
    @Transactional
    public void deleteUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // 1) purge les rôles liés à l’utilisateur
        utilisateur.getRoles().clear();
        utilisateurRepository.save(utilisateur);

        // 2) suppression de l’utilisateur
        utilisateurRepository.delete(utilisateur);
    }

    // Récupère un utilisateur par son id
    @Override
    public Optional<Utilisateur> getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id);
    }
}
