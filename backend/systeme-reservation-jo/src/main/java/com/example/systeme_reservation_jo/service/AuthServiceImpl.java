package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.payload.request.LoginRequest;
import com.example.systeme_reservation_jo.payload.request.SignupRequest;
import com.example.systeme_reservation_jo.payload.response.JwtAuthenticationResponse;
import com.example.systeme_reservation_jo.payload.response.MessageResponse;
import com.example.systeme_reservation_jo.repository.UtilisateurRepository;
import com.example.systeme_reservation_jo.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

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
    public ResponseEntity<?> register(SignupRequest signupRequest) {
        if (utilisateurRepository.existsByEmail(signupRequest.getEmail())) {
            return new ResponseEntity<>(new MessageResponse("Erreur : L'email est déjà utilisé!"), HttpStatus.BAD_REQUEST);
        }

        if (utilisateurRepository.existsByUsername(signupRequest.getUsername())) {
            return new ResponseEntity<>(new MessageResponse("Erreur : Le nom d'utilisateur est déjà utilisé!"), HttpStatus.BAD_REQUEST);
        }

        // Créer un nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(signupRequest.getUsername());
        utilisateur.setEmail(signupRequest.getEmail());
        utilisateur.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        utilisateur.setRoles(Collections.singleton("ROLE_USER"));

        utilisateurRepository.save(utilisateur);

        return ResponseEntity.ok(new MessageResponse("Utilisateur enregistré avec succès!")); // Code 200 OK
    }
}