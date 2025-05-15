package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.SystemeReservationJoApplication;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.StatutReservation;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    private Reservation createValidReservation() {
        Utilisateur utilisateur = new Utilisateur(1L, "testUser", "password", "test@example.com", null, null);
        Evenement evenement = new Evenement();
        evenement.setId(1L);
        Reservation reservation = new Reservation();
        reservation.setUtilisateur(utilisateur);
        reservation.setEvenement(evenement);
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setNombreBillets(2);
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        return reservation;
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UTILISATEUR"}) // 🔹 Simule un utilisateur authentifié
    void createReservation_ValidReservation_ReturnsCreatedReservation() throws Exception {
        Reservation reservation = createValidReservation();
        Mockito.when(reservationService.createReservation(Mockito.any(Reservation.class))).thenReturn(reservation);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombreBillets").value(2));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UTILISATEUR"}) // 🔹 Simule un utilisateur authentifié
    void getReservationById_ExistingId_ReturnsReservation() throws Exception {
        Reservation reservation = createValidReservation();
        Long reservationId = 1L;
        reservation.setId(reservationId);
        Mockito.when(reservationService.getReservationById(reservationId)).thenReturn(Optional.of(reservation));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservations/" + reservationId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(reservationId));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UTILISATEUR"}) // 🔹 Simule un utilisateur authentifié
    void deleteReservation_ExistingId_ReturnsNoContent() throws Exception {
        Long reservationId = 1L;
        Reservation reservation = createValidReservation();
        reservation.setId(reservationId);
        Mockito.when(reservationService.getReservationById(reservationId)).thenReturn(Optional.of(reservation));
        Mockito.doNothing().when(reservationService).deleteReservation(reservationId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reservations/" + reservationId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
