package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.repository.EvenementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
@Transactional
public class EvenementServiceImpl implements EvenementService {

    @Autowired
    private EvenementRepository evenementRepository;

    @Override
    public Evenement saveEvenement(Evenement evenement) {
        return evenementRepository.save(evenement);
    }

    @Override
    public List<Evenement> getAllEvenements() {
        return evenementRepository.findAll();
    }

    @Override
    public Optional<Evenement> getEvenementById(Long id) {
        return evenementRepository.findById(id);
    }

    @Override
    public Evenement updateEvenement(Long id, Evenement evenementDetails) {
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'id : " + id));

        evenement.setNom(evenementDetails.getNom());
        evenement.setDescription(evenementDetails.getDescription());
        evenement.setDateHeure(evenementDetails.getDateHeure());
        evenement.setLieu(evenementDetails.getLieu());
        evenement.setCapaciteTotale(evenementDetails.getCapaciteTotale());
        evenement.setPlacesRestantes(evenementDetails.getPlacesRestantes());
        evenement.setCategorie(evenementDetails.getCategorie());
        evenement.setPrix(evenementDetails.getPrix());


        return evenementRepository.save(evenement);
    }

    @Override
    public void deleteEvenement(Long id) {
        evenementRepository.deleteById(id);
    }

    @Override
    public List<Evenement> findEvenementsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return evenementRepository.findEvenementsBetweenDates(start, end);
    }
}