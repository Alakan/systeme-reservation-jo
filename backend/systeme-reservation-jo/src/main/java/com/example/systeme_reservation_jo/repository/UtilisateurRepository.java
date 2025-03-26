package com.example.systeme_reservation_jo.repository;

import com.example.systeme_reservation_jo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findByEmail(String email); // Recherche par email
    Boolean existsByEmail(String email);          // Vérifie si un email existe
    Optional<Utilisateur> findByUsername(String username); //On garde la méthode au cas où.
    Boolean existsByUsername(String username); //On garde la méthode au cas où.
}