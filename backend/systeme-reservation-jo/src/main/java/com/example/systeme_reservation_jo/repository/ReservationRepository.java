package com.example.systeme_reservation_jo.repository;

import com.example.systeme_reservation_jo.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByEvenement_Id(Long evenementId); // 🔹 Ajout de la méthode
}
