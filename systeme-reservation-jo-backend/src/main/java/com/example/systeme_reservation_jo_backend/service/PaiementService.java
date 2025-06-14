package com.example.systeme_reservation_jo_backend.service;

import com.example.systeme_reservation_jo_backend.model.Paiement;
import com.example.systeme_reservation_jo_backend.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface PaiementService {
    Paiement savePaiement(Paiement paiement);
    List<Paiement> getAllPaiements();
    Optional<Paiement> getPaiementById(Long id);
    Paiement updatePaiement(Long id, Paiement paiementDetails);
    void deletePaiement(Long id);

    List<Paiement> getPaiementsByReservation(Reservation reservation);
}
