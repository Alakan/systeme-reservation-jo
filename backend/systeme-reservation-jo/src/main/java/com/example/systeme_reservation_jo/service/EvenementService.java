package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Evenement;
import java.util.List;
import java.util.Optional;

public interface EvenementService {
    Evenement saveEvenement(Evenement evenement);
    List<Evenement> getAllEvenements();
    Optional<Evenement> getEvenementById(Long id);
    Evenement updateEvenement(Long id, Evenement evenementDetails);
    void deleteEvenement(Long id);
    List<Evenement> findEvenementsBetweenDates(java.time.LocalDateTime start, java.time.LocalDateTime end); //Pour la recherche par date

}