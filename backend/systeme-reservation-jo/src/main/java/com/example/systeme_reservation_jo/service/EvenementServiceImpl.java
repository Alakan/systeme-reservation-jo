package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.repository.EvenementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EvenementServiceImpl implements EvenementService {

    private final EvenementRepository evenementRepository;

    @Autowired
    public EvenementServiceImpl(EvenementRepository evenementRepository) {
        this.evenementRepository = evenementRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evenement> getAllEvenements() {
        return evenementRepository.findAll();
    }
/*
    @Override
    @Transactional(readOnly = true)
    public Evenement findEvenement(int id) {
        return evenementRepository.findById(id).orElse(null);
    } */

    @Override
    @Transactional
    public Evenement createEvenement(Evenement evenement) {
        // Validations (peuvent être externalisées dans une classe de validation séparée)
        if (evenement.getDateEvenement() == null || evenement.getDateEvenement().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date de l'événement doit être dans le futur.");
        }
        if (evenement.getLieu() == null || evenement.getLieu().trim().isEmpty()) {
            throw new IllegalArgumentException("Le lieu de l'événement ne peut pas être null ou vide.");
        }
        if (evenement.getCapaciteTotale() <= 0) {
            throw new IllegalArgumentException("La capacité totale doit être supérieure à zéro.");
        }
        // Enregistre l'événement dans la base de données
        return evenementRepository.save(evenement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evenement> findEvenementsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return evenementRepository.findEvenementsBetweenDates(start, end);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Evenement> getEvenementById(Integer id) {
        return evenementRepository.findById(id);
    }

    @Override
    @Transactional
    public Evenement updateEvenement(Integer id, Evenement evenementDetails) {
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé avec l'id : " + id));

        //Validations
        if (evenementDetails.getDateEvenement() == null || evenementDetails.getDateEvenement().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date de l'événement doit être dans le futur.");
        }
        if (evenementDetails.getLieu() == null || evenementDetails.getLieu().trim().isEmpty()) {
            throw new IllegalArgumentException("Le lieu de l'événement ne peut pas être vide.");
        }
        if (evenementDetails.getCapaciteTotale() <= 0) {
            throw new IllegalArgumentException("La capacité totale doit être supérieure à zéro.");
        }
        // Mettre à jour l'entité existante
        evenement.setTitre(evenementDetails.getTitre());
        evenement.setDescription(evenementDetails.getDescription());
        evenement.setDateEvenement(evenementDetails.getDateEvenement());
        evenement.setLieu(evenementDetails.getLieu());
        evenement.setCapaciteTotale(evenementDetails.getCapaciteTotale());
        evenement.setPlacesRestantes(evenementDetails.getPlacesRestantes()); // Assurez-vous que cette logique est correcte
        evenement.setCategorie(evenementDetails.getCategorie());
        evenement.setPrix(evenementDetails.getPrix());

        return evenementRepository.save(evenement);
    }



    @Override
    @Transactional(readOnly = true)
    public List<Evenement> searchEvenements(String motCle) {
        return evenementRepository.findByTitreContainingIgnoreCaseOrDescriptionContainingIgnoreCase(motCle, motCle);
    }

    @Override
    @Transactional
    public void deleteEvenement(Integer id) {
        // Récupère l'événement ou lance une exception s'il n'est pas trouvé
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé avec l'id : " + id));
        // Supprime l'événement de la base de données
        evenementRepository.delete(evenement);
    }
/*
    @Override
    @Transactional(readOnly = true)
    public List<Evenement> searchEvenements(String motCle) {
        return evenementRepository.findByTitreContainingIgnoreCaseOrDescriptionContainingIgnoreCase(motCle, motCle);
    }
    @Override
    @Transactional(readOnly = true)
    public List<Evenement> findByTitre(String titre) {
        return  evenementRepository.findByTitre(titre);
    }

    @Override
    @Transactional(readOnly = true)
    public  List<Evenement> searchEvenementsByTitre(String motCle) {
        return evenementRepository.findByTitreContainingIgnoreCase(motCle);
    }

    @Override
    @Transactional(readOnly = true)
    public  List<Evenement> searchEvenementsByDescription(String motCle) {
        return evenementRepository.findByDescriptionContainingIgnoreCase(motCle);
    } */
}