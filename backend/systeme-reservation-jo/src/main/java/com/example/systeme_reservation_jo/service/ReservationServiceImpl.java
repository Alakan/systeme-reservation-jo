package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.repository.ReservationRepository;
import com.example.systeme_reservation_jo.repository.EvenementRepository;
import com.example.systeme_reservation_jo.repository.PaiementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final EvenementRepository evenementRepository;
    private final PaiementRepository paiementRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository, EvenementRepository evenementRepository, PaiementRepository paiementRepository) {
        this.reservationRepository = reservationRepository;
        this.evenementRepository = evenementRepository;
        this.paiementRepository = paiementRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        Long evenementId = reservation.getEvenementId();
        if (evenementId == null) {
            throw new IllegalArgumentException("L'événement lié à la réservation est obligatoire !");
        }

        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé avec l'id : " + evenementId));

        reservation.setEvenement(evenement);
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public Reservation updateReservation(Long id, Reservation reservationDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'id : " + id));

        if (reservationDetails.getEvenementId() == null) {
            throw new IllegalArgumentException("Une réservation doit toujours être liée à un événement !");
        }

        Evenement evenement = evenementRepository.findById(reservationDetails.getEvenementId())
                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé avec l'id : " + reservationDetails.getEvenementId()));

        reservation.setUtilisateur(reservationDetails.getUtilisateur());
        reservation.setDateReservation(reservationDetails.getDateReservation());
        reservation.setNombreBillets(reservationDetails.getNombreBillets());
        reservation.setStatut(reservationDetails.getStatut());
        reservation.setEvenement(evenement);

        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'id : " + id));

        if (paiementRepository.existsByReservationId(id)) {
            throw new IllegalStateException("Impossible de supprimer la réservation : des paiements sont associés.");
        }

        reservationRepository.delete(reservation);
    }

    @Override
    public List<Reservation> getReservationsByUtilisateur(Long utilisateurId) {
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getUtilisateur().getId().equals(utilisateurId))
                .toList();
    }
}
