package com.example.systeme_reservation_jo.service;

// IMPORTS CORRECTS (selon vos informations)
import com.example.systeme_reservation_jo.model.Role;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.payload.request.LoginRequest;
import com.example.systeme_reservation_jo.payload.request.SignupRequest; // Utilisez SignupRequest, car RegisterRequest n'existe pas
import com.example.systeme_reservation_jo.payload.response.JwtAuthenticationResponse;
import com.example.systeme_reservation_jo.payload.response.MessageResponse;

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

import java.util.HashSet;
import java.util.Set;

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
        if (utilisateurRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Erreur: Le nom d'utilisateur est déjà pris!"));
        }

        if (utilisateurRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Erreur: L'email est déjà utilisé!"));
        }

        // Créer un nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(signupRequest.getUsername());
        utilisateur.setPassword(passwordEncoder.encode(signupRequest.getPassword())); // Encoder le mot de passe
        utilisateur.setEmail(signupRequest.getEmail());

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_UTILISATEUR); // Attribuer le rôle utilisateur par défaut
        utilisateur.setRoles(roles);

        utilisateurRepository.save(utilisateur);

        return ResponseEntity.ok(new MessageResponse("Utilisateur enregistré avec succès!"));
    }
}