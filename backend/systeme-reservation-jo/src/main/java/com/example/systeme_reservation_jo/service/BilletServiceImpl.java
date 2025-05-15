package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Billet;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.TypeBillet;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.repository.BilletRepository;
import com.example.systeme_reservation_jo.repository.EvenementRepository;
import com.example.systeme_reservation_jo.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BilletServiceImpl implements BilletService {

    private static final Logger logger = LoggerFactory.getLogger(BilletServiceImpl.class);

    @Autowired
    private BilletRepository billetRepository;

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public Billet saveBillet(Billet billet) {
        Long evenementId = billet.getEvenement().getId();
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec ID : " + evenementId));
        billet.setEvenement(evenement);

        Long reservationId = billet.getReservation().getId();
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec ID : " + reservationId));
        billet.setReservation(reservation);

        logger.info("Enregistrement du billet : {}", billet);
        return billetRepository.save(billet);
    }

    @Override
    public List<Billet> getAllBillets() {
        return billetRepository.findAll();
    }

    @Override
    public Optional<Billet> getBilletById(Long id) {
        logger.info("Recherche du billet avec ID : {}", id);
        return billetRepository.findById(id);
    }

    @Override
    public List<Billet> getBilletsByEvenement(Evenement evenement) {
        logger.info("Récupération des billets pour l'événement : {}", evenement.getId());
        return billetRepository.findByEvenement_Id(evenement.getId()); // 🔹 Correction ici
    }

    @Override
    public List<Billet> getBilletsByUtilisateur(Utilisateur utilisateur) {
        logger.info("Récupération des billets pour l'utilisateur avec ID : {}", utilisateur.getId());
        return billetRepository.findByUtilisateur(utilisateur);
    }

    @Override
    public List<Billet> getBilletsByStatut(TypeBillet statut) {
        logger.info("Récupération des billets avec le statut : {}", statut);
        return billetRepository.findByStatut(statut);
    }

    @Override
    public Billet updateBillet(Long id, Billet billetDetails) {
        Billet billet = billetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billet non trouvé avec l'id : " + id));
        billet.setEvenement(billetDetails.getEvenement());
        billet.setUtilisateur(billetDetails.getUtilisateur());
        billet.setDateReservation(billetDetails.getDateReservation());
        billet.setStatut(billetDetails.getStatut());
        billet.setType(billetDetails.getType());
        logger.info("Billet mis à jour avec ID : {}", id);
        return billetRepository.save(billet);
    }

    @Override
    public void deleteBillet(Long id) {
        logger.info("Suppression du billet avec ID : {}", id);
        billetRepository.deleteById(id);
    }
}
