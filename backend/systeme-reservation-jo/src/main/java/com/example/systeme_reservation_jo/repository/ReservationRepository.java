package com.example.systeme_reservation_jo.repository;

import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUtilisateur(Utilisateur utilisateur); //Pour recuperer les reservations par utilisateur
}