package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Billet;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.TypeBillet; // Importez l'enum TypeBillet
import com.example.systeme_reservation_jo.model.Utilisateur;

import java.util.List;
import java.util.Optional;

public interface BilletService {
    Billet saveBillet(Billet billet);
    List<Billet> getAllBillets();
    Optional<Billet> getBilletById(Integer id); // Integer
    Billet updateBillet(Integer id, Billet billetDetails); //Integer
    void deleteBillet(Integer id); // Integer
    List<Billet> getBilletsByEvenement(Evenement evenement);
    List<Billet> getBilletsByUtilisateur(Utilisateur utilisateur);
    List<Billet> getBilletsByStatut(TypeBillet statut); // Type modifié pour utiliser l'enum
}