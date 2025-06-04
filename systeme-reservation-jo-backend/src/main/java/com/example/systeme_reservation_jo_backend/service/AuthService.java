package com.example.systeme_reservation_jo_backend.service;

import com.example.systeme_reservation_jo_backend.payload.request.LoginRequest;
import com.example.systeme_reservation_jo_backend.payload.request.SignupRequest;
import com.example.systeme_reservation_jo_backend.payload.response.JwtAuthenticationResponse;
import com.example.systeme_reservation_jo_backend.payload.response.MessageResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    JwtAuthenticationResponse login(LoginRequest loginRequest); // Retourne un JWT
    ResponseEntity<?> register(SignupRequest signupRequest); // Gère l'inscription, retourne un message de succès ou d'erreur

}