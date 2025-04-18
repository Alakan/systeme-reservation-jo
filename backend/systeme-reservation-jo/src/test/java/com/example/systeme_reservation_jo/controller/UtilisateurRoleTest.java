package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.SystemeReservationJoApplication;
import com.example.systeme_reservation_jo.model.Role;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.repository.UtilisateurRepository;
import com.example.systeme_reservation_jo.service.UtilisateurService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@SpringBootTest(classes = SystemeReservationJoApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnableMethodSecurity // Assurez-vous que cette annotation est présente
public class UtilisateurRoleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private UtilisateurService utilisateurService;

    @MockBean
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @Bean
        public AuthenticationManager authenticationManager() {
            return new ProviderManager(Collections.singletonList(new TestingAuthenticationProvider()));
        }
    }

    // Test : Accès autorisé pour ADMINISTRATEUR
    @Test
    void accessProtectedEndpoint_AsAdmin_ReturnsOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/utilisateurs/admin")
                        .with(user("adminUser").roles("ADMINISTRATEUR")))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Test : Accès refusé pour UTILISATEUR
    @Test
    void accessProtectedEndpoint_AsUtilisateur_ReturnsForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/utilisateurs/admin")
                        .with(user("standardUser").roles("UTILISATEUR")))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    // Test : Mise à jour des rôles d'un utilisateur
    @Test
    void updateUtilisateurRoles_ReturnsUpdatedRoles() throws Exception {
        Utilisateur utilisateur = Utilisateur.builder()
                .username("updatableUser")
                .password("securePassword")
                .email("updatable@example.com")
                .roles(Set.of("ROLE_UTILISATEUR"))
                .build();

        Mockito.when(utilisateurService.getUtilisateurById(1))
                .thenReturn(Optional.of(utilisateur));

        Utilisateur utilisateurMisAJour = Utilisateur.builder()
                .username("updatableUser")
                .password("securePassword")
                .email("updatable@example.com")
                .roles(Set.of("ROLE_ADMINISTRATEUR"))
                .build();

        Mockito.when(utilisateurService.updateUtilisateur(Mockito.anyInt(), Mockito.any(Utilisateur.class)))
                .thenReturn(utilisateurMisAJour);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/utilisateurs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateurMisAJour)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0]").value("ROLE_ADMINISTRATEUR"));
    }
}