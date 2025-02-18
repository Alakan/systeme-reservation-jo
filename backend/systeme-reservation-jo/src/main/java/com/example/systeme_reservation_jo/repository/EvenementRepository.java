package com.example.systeme_reservation_jo.repository;

import com.example.systeme_reservation_jo.model.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    // Exemple de méthode de requête personnalisée avec JPQL:
    @Query("SELECT e FROM Evenement e WHERE e.dateHeure BETWEEN :start AND :end")
    List<Evenement> findEvenementsBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    //Tu pourrais aussi utiliser la derivation de requete:
    List<Evenement> findByDateHeureBetween(LocalDateTime start, LocalDateTime end);

    // Autres exemples de méthodes (que tu pourrais ajouter plus tard, si besoin):
    // List<Evenement> findByLieu(String lieu);
    // List<Evenement> findByCategorie(String categorie);
}