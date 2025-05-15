package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Reservation;
import java.util.Optional;
import java.util.List;

public interface ReservationService {
    List<Reservation> getAllReservations();
    Optional<Reservation> getReservationById(Long id);
    Reservation createReservation(Reservation reservation);
    Reservation updateReservation(Long id, Reservation reservationDetails);
    void deleteReservation(Long id);
    List<Reservation> getReservationsByUtilisateur(Long utilisateurId);
}
