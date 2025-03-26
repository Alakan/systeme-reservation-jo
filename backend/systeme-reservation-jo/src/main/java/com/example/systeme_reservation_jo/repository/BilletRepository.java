package com.example.systeme_reservation_jo.repository;

import com.example.systeme_reservation_jo.model.Billet;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.TypeBillet; // Importez l'enum TypeBillet
import com.example.systeme_reservation_jo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BilletRepository extends JpaRepository<Billet, Integer> {
    List<Billet> findByEvenement(Evenement evenement);
    List<Billet> findByUtilisateur(Utilisateur utilisateur); // Recherche les billets d'un utilisateur
    List<Billet> findByStatut(TypeBillet statut); // Recherche les billets par statut (TypeBillet) - Correction ici
}