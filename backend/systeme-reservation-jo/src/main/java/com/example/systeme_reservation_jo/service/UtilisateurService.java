package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Utilisateur;
import java.util.List;
import java.util.Optional;

public interface UtilisateurService {
    Utilisateur saveUtilisateur(Utilisateur utilisateur); // Enregistre un utilisateur (inscription)
    List<Utilisateur> getAllUtilisateurs(); // Récupère tous les utilisateurs
    Optional<Utilisateur> getUtilisateurById(Integer id); // Récupère un utilisateur par son ID
    Utilisateur updateUtilisateur(Integer id, Utilisateur utilisateurDetails); // Met à jour un utilisateur
    void deleteUtilisateur(Integer id); // Supprime un utilisateur
    Optional<Utilisateur> getUtilisateurByEmail(String email); //Pour récupérer un utilisateur par son email.
    boolean existsByEmail(String email);//Pour verifier l'existance d'un utilisateur par email.
    boolean existsByUsername(String username); //Pour verifier si le username existe déjà.
}