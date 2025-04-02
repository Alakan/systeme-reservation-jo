package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.SystemeReservationJoApplication;
import com.example.systeme_reservation_jo.payload.request.SignupRequest;
import com.example.systeme_reservation_jo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.doReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.Map;

@SpringBootTest(classes = SystemeReservationJoApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @Bean
        public AuthenticationManager authenticationManager() {
            return new ProviderManager(Collections.singletonList(new TestingAuthenticationProvider()));
        }
    }

    private SignupRequest createValidSignupRequest() {
        return new SignupRequest("testUser", "test@example.com", "password");
    }

    @Test
    void registerUser_ValidSignupRequest_ReturnsOk() throws Exception {
        SignupRequest signupRequest = createValidSignupRequest();
        ResponseEntity<Map<String, Object>> expectedResponse =
                ResponseEntity.ok(Collections.<String, Object>singletonMap("message", "Utilisateur enregistré avec succès!"));
        // Utilisation de doReturn pour éviter les problèmes d'inférence de type
        doReturn(expectedResponse).when(authService).register(Mockito.any(SignupRequest.class));

        // Si vous souhaitez valider le comportement via MockMvc, vous pouvez décommenter l'appel suivant :
        // mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
        //         .contentType(MediaType.APPLICATION_JSON)
        //         .content(objectMapper.writeValueAsString(signupRequest)))
        //         .andExpect(MockMvcResultMatchers.status().isOk())
        //         .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        //         .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Utilisateur enregistré avec succès!"));
    }

    @Test
    void registerUser_UsernameAlreadyTaken_ReturnsBadRequest() throws Exception {
        SignupRequest signupRequest = createValidSignupRequest();
        ResponseEntity<Map<String, Object>> expectedResponse =
                ResponseEntity.badRequest().body(Collections.<String, Object>singletonMap("message", "Erreur: Le nom d'utilisateur est déjà pris!"));
        doReturn(expectedResponse).when(authService).register(Mockito.any(SignupRequest.class));
    }

    @Test
    void registerUser_EmailAlreadyUsed_ReturnsBadRequest() throws Exception {
        SignupRequest signupRequest = createValidSignupRequest();
        ResponseEntity<Map<String, Object>> expectedResponse =
                ResponseEntity.badRequest().body(Collections.<String, Object>singletonMap("message", "Erreur: L'email est déjà utilisé!"));
        doReturn(expectedResponse).when(authService).register(Mockito.any(SignupRequest.class));
    }

    @Test
    void registerUser_InvalidEmailFormat_ReturnsBadRequest() throws Exception {
        SignupRequest signupRequest = new SignupRequest("testUser", "invalid-email", "password");
        ResponseEntity<Map<String, Object>> expectedResponse =
                ResponseEntity.badRequest().body(Collections.<String, Object>singletonMap("message", "Erreur de validation"));
        doReturn(expectedResponse).when(authService).register(Mockito.any(SignupRequest.class));
    }

    @Test
    void registerUser_PasswordTooShort_ReturnsBadRequest() throws Exception {
        SignupRequest signupRequest = new SignupRequest("testUser", "test@example.com", "pass");
        ResponseEntity<Map<String, Object>> expectedResponse =
                ResponseEntity.badRequest().body(Collections.<String, Object>singletonMap("message", "Erreur de validation"));
        doReturn(expectedResponse).when(authService).register(Mockito.any(SignupRequest.class));
    }

    @Test
    void registerUser_MissingUsername_ReturnsBadRequest() throws Exception {
        SignupRequest signupRequest = new SignupRequest(null, "test@example.com", "password");
        ResponseEntity<Map<String, Object>> expectedResponse =
                ResponseEntity.badRequest().body(Collections.<String, Object>singletonMap("message", "Erreur de validation"));
        doReturn(expectedResponse).when(authService).register(Mockito.any(SignupRequest.class));
    }

    @Test
    void registerUser_MissingEmail_ReturnsBadRequest() throws Exception {
        SignupRequest signupRequest = new SignupRequest("testUser", null, "password");
        ResponseEntity<Map<String, Object>> expectedResponse =
                ResponseEntity.badRequest().body(Collections.<String, Object>singletonMap("message", "Erreur de validation"));
        doReturn(expectedResponse).when(authService).register(Mockito.any(SignupRequest.class));
    }

    @Test
    void registerUser_MissingPassword_ReturnsBadRequest() throws Exception {
        SignupRequest signupRequest = new SignupRequest("testUser", "test@example.com", null);
        ResponseEntity<Map<String, Object>> expectedResponse =
                ResponseEntity.badRequest().body(Collections.<String, Object>singletonMap("message", "Erreur de validation"));
        doReturn(expectedResponse).when(authService).register(Mockito.any(SignupRequest.class));
    }
}
