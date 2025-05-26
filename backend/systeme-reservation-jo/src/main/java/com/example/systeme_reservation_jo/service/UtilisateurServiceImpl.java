package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Retourne la liste complète des utilisateurs
    @Override
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    // Retourne uniquement les utilisateurs "administrateurs"
    @Override
    public List<Utilisateur> getAllAdminUsers() {
        return utilisateurRepository.findByRoles_Name("ROLE_ADMINISTRATEUR");
    }

    // Vérifie l'existence d'un utilisateur par email
    @Override
    public boolean existsByEmail(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    // Recherche un utilisateur par son email
    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    // Sauvegarde un nouvel utilisateur avec encodage du mot de passe, si nécessaire
    @Override
    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        if (utilisateur.getPassword() != null
                && !utilisateur.getPassword().isBlank()
                && !utilisateur.getPassword().startsWith("$2a$")) {
            utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        }
        return utilisateurRepository.save(utilisateur);
    }

    // Mise à jour de l'utilisateur, y compris le mot de passe et les rôles
    @Override
    public Utilisateur updateUtilisateur(Long id, Utilisateur utilisateurDetails) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Mise à jour des champs basiques
        utilisateur.setUsername(utilisateurDetails.getUsername());
        utilisateur.setEmail(utilisateurDetails.getEmail());

        // Encodage du mot de passe si un nouveau mot de passe est fourni
        if (utilisateurDetails.getPassword() != null && !utilisateurDetails.getPassword().isBlank()) {
            if (!utilisateurDetails.getPassword().startsWith("$2a$")) {
                utilisateur.setPassword(passwordEncoder.encode(utilisateurDetails.getPassword()));
            } else {
                utilisateur.setPassword(utilisateurDetails.getPassword());
            }
        }

        // Mise à jour des rôles si nécessaire
        utilisateur.setRoles(utilisateurDetails.getRoles());
        return utilisateurRepository.save(utilisateur);
    }

    // Suppression de l'utilisateur par son identifiant
    @Override
    public void deleteUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }

    // Récupération d'un utilisateur par son id
    @Override
    public Optional<Utilisateur> getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id);
    }
}
