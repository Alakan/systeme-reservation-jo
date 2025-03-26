package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Evenement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EvenementService {

    /*
     * Récupère tous les événements.
     * @return Une liste de tous les événements.
     */
    List<Evenement> getAllEvenements();

    /*
     * Crée un nouvel événement.
     * @param evenement L'événement à créer.
     * @return L'événement créé (avec son ID généré).
     */
    Evenement createEvenement(Evenement evenement);

    /*
     * Recherche les événements entre deux dates.
     * @param start La date de début (inclusive).
     * @param end La date de fin (inclusive).
     * @return Une liste des événements qui se déroulent entre les deux dates.
     */
    List<Evenement> findEvenementsBetweenDates(LocalDateTime start, LocalDateTime end);

    /*
     * Récupère un événement par son ID.  Utilise Optional pour gérer le cas où l'ID n'existe pas.
     * @param id L'ID de l'événement.
     * @return Un Optional contenant l'événement, ou un Optional vide si l'événement n'est pas trouvé.
     */
    Optional<Evenement> getEvenementById(Integer id);

    /*
     * Recherche un evenement avec son id.
     * @param id
     * @return l'évenement.
     */
   /* Evenement findEvenement(int id);

    /**
     * Met à jour un événement existant.
     * @param id L'ID de l'événement à mettre à jour.
     * @param evenementDetails Les nouvelles données de l'événement.
     * @return L'événement mis à jour.
     * @throws EntityNotFoundException Si l'événement avec l'ID donné n'est pas trouvé.
     */
    Evenement updateEvenement(Integer id, Evenement evenementDetails);

    /*
     * Supprime un événement par son ID.
     * @param id L'ID de l'événement à supprimer.
     * @throws EntityNotFoundException Si l'événement avec l'ID donné n'est pas trouvé.
     */
    void deleteEvenement(Integer id);

    /*
     * Recherche des événements par mot-clé (dans le titre ou la description).
     *
     * @param motCle Le mot-clé à rechercher.
     * @return Une liste d'événements correspondant au mot-clé.
     */
    List<Evenement> searchEvenements(String motCle);

    /*
     * Trouve tous les evenements par titre.
     * @param titre
     * @return

    List<Evenement> findByTitre(String titre); */

    /*
     * Recherche des événements par titre.
     * @param motCle
     * @return

    List<Evenement> searchEvenementsByTitre(String motCle); */

    /*
     * Recherche des événements par description.
     * @param motCle
     * @return

    List<Evenement> searchEvenementsByDescription(String motCle); */
}