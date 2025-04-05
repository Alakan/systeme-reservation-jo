package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.SystemeReservationJoApplication;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.service.EvenementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

@SpringBootTest(classes = SystemeReservationJoApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class EvenementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EvenementService evenementService;

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

    private Evenement createValidEvenement() {
        return new Evenement(1, "Nom Evenement", "Description", LocalDateTime.now().plusDays(1), "Lieu", 100, 90, "Sport", BigDecimal.TEN);
    }

    @Test
    void getAllEvenements_NoAuthentication_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/evenements"))
                .andExpect(MockMvcResultMatchers.status().isOk()); // Modifié pour attendre 200
    }

    @Test
    void getEvenementById_ExistingId_ReturnsEvenement() throws Exception {
        Evenement evenement = createValidEvenement();
        Mockito.when(evenementService.getEvenementById(1)).thenReturn(Optional.of(evenement));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/evenements/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.titre").value("Nom Evenement"));
    }

    @Test
    void getEvenementById_NonExistingId_ReturnsNotFound() throws Exception {
        Mockito.when(evenementService.getEvenementById(2)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/evenements/2"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void createEvenement_ValidEvenement_ReturnsCreatedEvenement() throws Exception {
        Evenement evenement = createValidEvenement();
        Mockito.when(evenementService.createEvenement(Mockito.any(Evenement.class))).thenReturn(evenement);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/evenements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evenement)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.titre").value("Nom Evenement"));
    }

    @Test
    void createEvenement_InvalidEvenement_ReturnsBadRequest() throws Exception {
        Evenement evenement = new Evenement();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/evenements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evenement)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateEvenement_ValidEvenement_ReturnsUpdatedEvenement() throws Exception {
        Evenement existingEvenement = createValidEvenement();
        Evenement updatedEvenement = new Evenement(1, "Nom Modifié", "Description", LocalDateTime.now().plusDays(2), "Autre Lieu", 120, 100, "Autre Sport", BigDecimal.valueOf(20));
        Mockito.when(evenementService.getEvenementById(1)).thenReturn(Optional.of(existingEvenement));
        Mockito.when(evenementService.updateEvenement(Mockito.eq(1), Mockito.any(Evenement.class))).thenReturn(updatedEvenement);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/evenements/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEvenement)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.titre").value("Nom Modifié"));
    }

    @Test
    void updateEvenement_NonExistingId_ReturnsNotFound() throws Exception {
        Evenement updatedEvenement = new Evenement(2, "Nom Modifié", "Description", LocalDateTime.now().plusDays(2), "Autre Lieu", 120, 100, "Autre Sport", BigDecimal.valueOf(20));
        Mockito.when(evenementService.getEvenementById(2)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/evenements/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEvenement)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteEvenement_ExistingId_ReturnsNoContent() throws Exception {
        Integer evenementId = 1;
        Mockito.when(evenementService.getEvenementById(evenementId)).thenReturn(Optional.of(createValidEvenement()));
        Mockito.doNothing().when(evenementService).deleteEvenement(evenementId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/evenements/" + evenementId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteEvenement_NonExistingId_ReturnsNotFound() throws Exception {
        Integer evenementId = 2;
        Mockito.when(evenementService.getEvenementById(evenementId)).thenReturn(Optional.empty());
        Mockito.doNothing().when(evenementService).deleteEvenement(evenementId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/evenements/" + evenementId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testFindEvenementsBetweenDates() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(7);
        Evenement evenement1 = createValidEvenement();
        Evenement evenement2 = new Evenement(2, "Autre Evenement", "Description", LocalDateTime.now().plusDays(3), "Lieu 2", 50, 40, "Course", BigDecimal.valueOf(15));
        List<Evenement> evenements = List.of(evenement1, evenement2);

        Mockito.when(evenementService.findEvenementsBetweenDates(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(evenements);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/evenements/between-dates")
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }

    @Test
    void createEvenement_AdminUser_ReturnsCreated() throws Exception {
        // Simuler un utilisateur avec le rôle ADMINISTRATEUR
        org.springframework.security.core.userdetails.User adminUser =
                new org.springframework.security.core.userdetails.User("admin", "password", Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMINISTRATEUR")));
        org.springframework.security.core.Authentication authentication =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(adminUser, adminUser.getPassword(), adminUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Evenement evenement = createValidEvenement();
        Mockito.when(evenementService.createEvenement(Mockito.any(Evenement.class))).thenReturn(evenement);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/evenements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evenement)))
                .andExpect(MockMvcResultMatchers.status().isCreated()); // Vérifier le statut 201 Created
    }
}