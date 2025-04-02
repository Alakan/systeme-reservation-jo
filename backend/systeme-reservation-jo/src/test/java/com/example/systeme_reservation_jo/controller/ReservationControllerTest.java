package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.SystemeReservationJoApplication;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.StatutReservation;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@SpringBootTest(classes = SystemeReservationJoApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @org.springframework.boot.test.context.TestConfiguration
    @EnableJpaRepositories(basePackages = "com.example.systeme_reservation_jo.repository")
    static class TestConfig {
        @Bean
        public AuthenticationManager authenticationManager() {
            return new ProviderManager(Collections.singletonList(new TestingAuthenticationProvider()));
        }
    }

    private Reservation createValidReservation() {
        Reservation reservation = new Reservation();
        reservation.setUtilisateur(new Utilisateur(1, "testUser", "password", "test@example.com", null));
        reservation.setEvenementId(1); // ID de l'événement
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setNombreBillets(2);
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        return reservation;
    }

    @Test
    void createReservation_ValidReservation_ReturnsCreatedReservation() throws Exception {
        Reservation reservation = createValidReservation();
        Mockito.when(reservationService.saveReservation(Mockito.any(Reservation.class))).thenReturn(reservation);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombreBillets").value(2));
    }

    @Test
    void createReservation_InvalidReservation_ReturnsBadRequest() throws Exception {
        Reservation reservation = new Reservation(); // Création d'une réservation invalide (champs manquants)

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()); // Vérifier le statut 400
    }

    @Test
    void createReservation_EventNotFound_ReturnsNotFound() throws Exception {
        Reservation reservation = createValidReservation();
        int evenementId = reservation.getEvenementId();
        Mockito.when(reservationService.saveReservation(Mockito.any(Reservation.class)))
                .thenThrow(new EntityNotFoundException("Événement non trouvé avec l'id : " + evenementId));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(MockMvcResultMatchers.status().isNotFound()); // Vérifier le statut 404
    }

    @Test
    void createReservation_NotEnoughPlaces_ReturnsBadRequest() throws Exception {
        Reservation reservation = createValidReservation();
        int evenementId = reservation.getEvenementId();
        Mockito.when(reservationService.saveReservation(Mockito.any(Reservation.class)))
                .thenThrow(new IllegalStateException("Pas assez de places disponibles pour l'événement : ..."));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()); // Vérifier le statut 400
    }

    @Test
    void getReservationById_ExistingId_ReturnsReservation() throws Exception {
        Reservation reservation = createValidReservation();
        long reservationId = 1L;
        reservation.setId(reservationId);
        Mockito.when(reservationService.getReservationById(reservationId)).thenReturn(Optional.of(reservation));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservations/" + reservationId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(reservationId));
    }

    @Test
    void getReservationById_NonExistingId_ReturnsNotFound() throws Exception {
        long reservationId = 2L;
        Mockito.when(reservationService.getReservationById(reservationId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservations/" + reservationId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteReservation_ExistingId_ReturnsNoContent() throws Exception {
        long reservationId = 1L;
        Reservation reservation = createValidReservation();
        reservation.setId(reservationId);
        Mockito.when(reservationService.getReservationById(reservationId)).thenReturn(Optional.of(reservation));
        Mockito.doNothing().when(reservationService).deleteReservation(reservationId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reservations/" + reservationId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteReservation_NonExistingId_ReturnsNotFound() throws Exception {
        long reservationId = 2L;
        Mockito.when(reservationService.getReservationById(reservationId)).thenReturn(Optional.empty());
        Mockito.doNothing().when(reservationService).deleteReservation(reservationId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reservations/" + reservationId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateReservation_ValidReservation_ReturnsUpdatedReservation() throws Exception {
        // Créer une réservation existante
        Reservation existingReservation = createValidReservation();
        long reservationId = 1L;
        existingReservation.setId(reservationId);
        Mockito.when(reservationService.getReservationById(reservationId)).thenReturn(Optional.of(existingReservation));

        // Créer les détails de la mise à jour
        Reservation updatedDetails = createValidReservation();
        updatedDetails.setNombreBillets(3);
        updatedDetails.setStatut(StatutReservation.CONFIRMEE);
        Mockito.when(reservationService.updateReservation(Mockito.eq(reservationId), Mockito.any(Reservation.class))).thenReturn(updatedDetails);

        // Effectuer la requête PUT
        mockMvc.perform(MockMvcRequestBuilders.put("/api/reservations/" + reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk()) // Vérifier le statut 200 OK
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombreBillets").value(3)) // Vérifier un champ mis à jour
                .andExpect(MockMvcResultMatchers.jsonPath("$.statut").value("CONFIRMEE")); // Vérifier un autre champ mis à jour
    }

    @Test
    void updateReservation_NonExistingId_ReturnsNotFound() throws Exception {
        long reservationId = 2L;
        Mockito.when(reservationService.getReservationById(reservationId)).thenReturn(Optional.empty());

        Reservation updatedDetails = createValidReservation();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/reservations/" + reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(MockMvcResultMatchers.status().isNotFound()); // Vérifier le statut 404 Not Found
    }

}