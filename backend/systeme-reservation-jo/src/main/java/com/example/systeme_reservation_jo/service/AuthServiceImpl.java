package com.example.systeme_reservation_jo.service;

// IMPORTS CORRECTS (selon vos informations)
import com.example.systeme_reservation_jo.payload.request.LoginRequest;
import com.example.systeme_reservation_jo.payload.request.SignupRequest; // Utilisez SignupRequest, car RegisterRequest n'existe pas
import com.example.systeme_reservation_jo.payload.response.JwtAuthenticationResponse;
// import com.example.systeme_reservation_jo.payload.response.MessageResponse; //  Importez si vous en avez besoin dans la méthode register

import com.example.systeme_reservation_jo.repository.UtilisateurRepository;
import com.example.systeme_reservation_jo.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Importez ResponseEntity
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public JwtAuthenticationResponse login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return new JwtAuthenticationResponse(jwt);
    }

    @Override
    public ResponseEntity<?> register(SignupRequest signupRequest) { // Utilisez SignupRequest
        // REMPLACEZ CECI par votre code. Utilisez SignupRequest.
        // Vous devrez probablement retourner un ResponseEntity.
        //Exemple :
        return ResponseEntity.ok().body("Inscription réussie !"); // Simple exemple - Adaptez à votre logique
    }
}