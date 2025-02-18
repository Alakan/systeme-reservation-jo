package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.Utilisateur;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    Reservation saveReservation(Reservation reservation);
    List<Reservation> getAllReservations();
    Optional<Reservation> getReservationById(Long id);
    Reservation updateReservation(Long id, Reservation reservationDetails);
    void deleteReservation(Long id);
    List<Reservation> getReservationsByUtilisateur(Utilisateur utilisateur);
}