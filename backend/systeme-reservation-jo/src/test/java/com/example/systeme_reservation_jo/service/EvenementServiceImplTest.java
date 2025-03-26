package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.repository.EvenementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvenementServiceImplTest {

    @Mock
    private EvenementRepository evenementRepository;

    @InjectMocks
    private EvenementServiceImpl evenementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void getAllEvenements_ReturnsListOfEvenements() {
        // Arrange
        Evenement evenement1 = new Evenement(1, "Evenement 1", "Description 1", LocalDateTime.now().plusDays(1), "Lieu 1", 100,100, "categorie", BigDecimal.valueOf(25.5));
        Evenement evenement2 = new Evenement(2, "Evenement 2", "Description 2", LocalDateTime.now().plusDays(2), "Lieu 2", 50,50, "categorie", BigDecimal.valueOf(30.0));
        List<Evenement> evenements = Arrays.asList(evenement1, evenement2);

        when(evenementRepository.findAll()).thenReturn(evenements);

        // Act
        List<Evenement> result = evenementService.getAllEvenements();

        // Assert
        assertEquals(2, result.size());
        assertEquals(evenements, result);
        verify(evenementRepository, times(1)).findAll();
    }

    @Test
    void getEvenementById_ExistingId_ReturnsEvenement() {
        // Arrange
        Integer id = 1;
        Evenement evenement = new Evenement(id, "Evenement 1", "Description 1", LocalDateTime.now().plusDays(1), "Lieu 1", 100,100, "categorie", BigDecimal.valueOf(25.5));
        when(evenementRepository.findById(id)).thenReturn(Optional.of(evenement));

        // Act
        Optional<Evenement> result = evenementService.getEvenementById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(evenement, result.get());
        verify(evenementRepository, times(1)).findById(id);
    }

    @Test
    void getEvenementById_NonExistingId_ReturnsEmptyOptional() {
        // Arrange
        Integer id = 1;
        when(evenementRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Evenement> result = evenementService.getEvenementById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(evenementRepository, times(1)).findById(id);
    }

    @Test
    void saveEvenement_ValidEvenement_ReturnsSavedEvenement() {
        // Arrange
        Evenement evenement = new Evenement(null, "Evenement Valide", "Description", LocalDateTime.now().plusDays(1), "Lieu Valide", 100, 100, "categorie", new BigDecimal("25.5"));
        // On simule le comportement de la base (qui va ajouter l'ID).
        Evenement evenementAvecId = new Evenement(1, "Evenement Valide", "Description", LocalDateTime.now().plusDays(1), "Lieu Valide", 100, 100, "categorie", new BigDecimal("25.5"));

        when(evenementRepository.save(any(Evenement.class))).thenReturn(evenementAvecId);

        // Act
        Evenement result = evenementService.createEvenement(evenement);

        // Assert
        assertNotNull(result);
        assertEquals(evenementAvecId, result); // On compare avec l'evenement qui a l'ID.
        assertEquals(1, result.getId()); // On verifie que l'ID est set.
        verify(evenementRepository, times(1)).save(evenement);
    }
    @Test
    void saveEvenement_DateInPast_ThrowsIllegalArgumentException() {
        // Arrange
        Evenement evenement = new Evenement(null, "Evenement Invalide", "Description", LocalDateTime.now().minusDays(1), "Lieu", 100, 100,"categorie", new BigDecimal("25.50"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> evenementService.createEvenement(evenement)); //saveEvenement est devenu createEvenement
        verify(evenementRepository, never()).save(any(Evenement.class));
    }
    @Test
    void saveEvenement_NullLieu_ThrowsIllegalArgumentException(){
        // Arrange
        Evenement evenement = new Evenement(null, "Evenement Invalide", "Description", LocalDateTime.now().plusDays(1), null, 100, 100, "categorie", new BigDecimal("25.50"));

        // Act & Assert (On vérifie qu'une exception est lancée)
        assertThrows(IllegalArgumentException.class, () -> evenementService.createEvenement(evenement)); //saveEvenement est devenu createEvenement
        verify(evenementRepository, never()).save(any(Evenement.class)); // Vérifie que save n'a jamais été appelé
    }

    @Test
    void saveEvenement_EmptyLieu_ThrowsIllegalArgumentException(){
        // Arrange
        Evenement evenement = new Evenement(null, "Evenement Invalide", "Description", LocalDateTime.now().plusDays(1), "", 100, 100,"categorie", new BigDecimal("25.50"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> evenementService.createEvenement(evenement)); //saveEvenement est devenu createEvenement
        verify(evenementRepository, never()).save(any(Evenement.class)); // Vérifie que save n'a jamais été appelé
    }
    @Test
    void saveEvenement_ZeroPlace_ThrowsIllegalArgumentException(){
        // Arrange
        Evenement evenement = new Evenement(null, "Evenement Invalide", "Description", LocalDateTime.now().plusDays(1), "Lieu", 0, 0,"categorie", new BigDecimal("25.50"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> evenementService.createEvenement(evenement));//saveEvenement est devenu createEvenement
        verify(evenementRepository, never()).save(any(Evenement.class)); // Vérifie que save n'a jamais été appelé
    }
    @Test
    void saveEvenement_NegativePlace_ThrowsIllegalArgumentException(){
        // Arrange
        Evenement evenement = new Evenement(null, "Evenement Invalide", "Description", LocalDateTime.now().plusDays(1), "Lieu", -22, 0,"categorie", new BigDecimal("25.50"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> evenementService.createEvenement(evenement));//saveEvenement est devenu createEvenement
        verify(evenementRepository, never()).save(any(Evenement.class)); // Vérifie que save n'a jamais été appelé
    }
    @Test
    void updateEvenement_ValidEvenement_ReturnsUpdatedEvenement() {
        // Arrange
        Integer id = 1;
        Evenement existingEvenement =  new Evenement(id, "Evenement Original", "Description", LocalDateTime.now().plusDays(1), "Lieu Original", 100, 100, "categorie", BigDecimal.valueOf(25.5));
        Evenement updatedDetails =  new Evenement(id, "Evenement Modifié", "Description", LocalDateTime.now().plusDays(2), "Nouveau Lieu", 150, 150,"categorie", BigDecimal.valueOf(25.5));


        when(evenementRepository.findById(id)).thenReturn(Optional.of(existingEvenement));
        when(evenementRepository.save(any(Evenement.class))).thenAnswer(i -> i.getArguments()[0]); //Retourne l'argument qui lui est passé

        // Act
        Evenement result = evenementService.updateEvenement(id, updatedDetails);

        // Assert
        assertNotNull(result);
        assertEquals("Evenement Modifié", result.getTitre());
        assertEquals("Nouveau Lieu", result.getLieu());
        assertEquals(150, result.getCapaciteTotale()); //CORRECTION : utiliser getCapaciteTotale
        verify(evenementRepository, times(1)).findById(id);
        verify(evenementRepository, times(1)).save(existingEvenement);
    }

    @Test
    void updateEvenement_NonExistingId_ThrowsEntityNotFoundException() {
        // Arrange
        Integer id = 1;
        Evenement updatedDetails =   new Evenement(id, "Evenement Modifié", "Description", LocalDateTime.now().plusDays(2), "Nouveau Lieu", 150, 150,"categorie", BigDecimal.valueOf(25.5));
        when(evenementRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> evenementService.updateEvenement(id, updatedDetails));
        verify(evenementRepository, times(1)).findById(id);
        verify(evenementRepository, never()).save(any(Evenement.class));
    }
    @Test
    void updateEvenement_DateInPast_ThrowsIllegalArgumentException() {
        //Arrange
        Integer id = 1;
        Evenement existingEvenement =  new Evenement(id, "Evenement Original", "Description", LocalDateTime.now().plusDays(1), "Lieu Original", 100, 100,"categorie", BigDecimal.valueOf(25.5));
        Evenement updatedDetails =  new Evenement(id, "Evenement Modifié", "Description", LocalDateTime.now().minusDays(1), "Nouveau Lieu", 150, 150,"categorie", BigDecimal.valueOf(25.5));

        when(evenementRepository.findById(id)).thenReturn(Optional.of(existingEvenement));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> evenementService.updateEvenement(id, updatedDetails));
        verify(evenementRepository, times(1)).findById(id);
        verify(evenementRepository, never()).save(any(Evenement.class));
    }
    @Test
    void deleteEvenement_ExistingId_DeletesEvenement() {
        // Arrange
        Integer id = 1;
        Evenement evenement = new Evenement(id, "Evenement Original", "Description", LocalDateTime.now().plusDays(1), "Lieu Original", 100, 100,"categorie", BigDecimal.valueOf(25.5));

        when(evenementRepository.findById(id)).thenReturn(Optional.of(evenement));
        doNothing().when(evenementRepository).delete(evenement); // doNothing() parce que delete() ne retourne rien

        // Act
        evenementService.deleteEvenement(id);

        // Assert
        verify(evenementRepository, times(1)).findById(id); //On verifie que findById a été appelé.
        verify(evenementRepository, times(1)).delete(evenement);
    }

    @Test
    void deleteEvenement_NonExistingId_ThrowsEntityNotFoundException() {
        // Arrange
        Integer id = 1;
        when(evenementRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> evenementService.deleteEvenement(id));
        verify(evenementRepository, times(1)).findById(id);
        verify(evenementRepository, never()).deleteById(anyInt()); // On utilise anyInt() ici
    }
    @Test
    void findEvenementsBetweenDates_ReturnsListOfEvenements() {
        // Arrange
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(7);
        Evenement evenement1 =  new Evenement(1,"Evenement 1","Description" ,start.plusDays(1), "Lieu 1", 100, 100,"categorie", BigDecimal.valueOf(25.5));
        Evenement evenement2 =  new Evenement(2, "Evenement 2","Description", start.plusDays(2), "Lieu 2", 50, 50,"categorie", BigDecimal.valueOf(25.5));
        List<Evenement> evenements = Arrays.asList(evenement1, evenement2);

        when(evenementRepository.findEvenementsBetweenDates(start, end)).thenReturn(evenements);

        // Act
        List<Evenement> result = evenementService.findEvenementsBetweenDates(start, end);

        // Assert
        assertEquals(2, result.size());
        assertEquals(evenements, result);
        verify(evenementRepository, times(1)).findEvenementsBetweenDates(start, end); //Utilisation de findByDateEvenementBetween
    }
}