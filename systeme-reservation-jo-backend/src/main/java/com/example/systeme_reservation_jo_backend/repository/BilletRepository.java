package com.example.systeme_reservation_jo_backend.repository;

import com.example.systeme_reservation_jo_backend.model.Billet;
import com.example.systeme_reservation_jo_backend.model.StatutBillet;
import com.example.systeme_reservation_jo_backend.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BilletRepository extends JpaRepository<Billet, Long> {
    List<Billet> findByEvenement_Id(Long evenementId);
    List<Billet> findByUtilisateur(Utilisateur utilisateur);
    List<Billet> findByStatut(StatutBillet statut);
    boolean existsByNumeroBillet(String numeroBillet);

    // Méthode classique pour récupérer par réservation (si besoin)
    Optional<Billet> findByReservation_Id(Long reservationId);

    // Méthode avec fetch join afin de récupérer l'objet Evenement directement
    @Query("SELECT b FROM Billet b JOIN FETCH b.evenement WHERE b.reservation.id = :reservationId")
    Optional<Billet> findByReservationIdWithEvenement(@Param("reservationId") Long reservationId);
}
