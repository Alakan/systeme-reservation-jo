package com.example.systeme_reservation_jo.repository;

import com.example.systeme_reservation_jo.model.Billet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.Utilisateur;


public interface BilletRepository extends JpaRepository<Billet, Long> {
    List<Billet> findByEvenement(Evenement evenement);
    List<Billet> findByUtilisateur(Utilisateur utilisateur); // Recherche les billets d'un utilisateur
    List<Billet> findByStatut(String statut); // Recherche les billets par statut (RESERVE, ANNULE, etc.)
}