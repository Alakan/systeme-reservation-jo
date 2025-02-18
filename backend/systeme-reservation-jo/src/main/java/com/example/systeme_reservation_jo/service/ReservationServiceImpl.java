package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public Reservation updateReservation(Long id, Reservation reservationDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation non trouvé avec l'id : " + id));

        reservation.setUtilisateur(reservationDetails.getUtilisateur());
        reservation.setBillets(reservationDetails.getBillets()); // Attention à la gestion des billets!
        reservation.setDateReservation(reservationDetails.getDateReservation());
        reservation.setNombreBillets(reservationDetails.getNombreBillets());
        reservation.setStatut(reservationDetails.getStatut());
        return reservationRepository.save(reservation);
    }

    @Override
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    @Override
    public List<Reservation> getReservationsByUtilisateur(Utilisateur utilisateur) {
        return reservationRepository.findByUtilisateur(utilisateur);
    }

}