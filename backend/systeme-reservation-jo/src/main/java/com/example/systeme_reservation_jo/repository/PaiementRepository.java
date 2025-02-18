package com.example.systeme_reservation_jo.repository;

import com.example.systeme_reservation_jo.model.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.systeme_reservation_jo.model.Reservation;
import java.util.Optional;


public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Optional<Paiement> findByReservation(Reservation reservation);
}