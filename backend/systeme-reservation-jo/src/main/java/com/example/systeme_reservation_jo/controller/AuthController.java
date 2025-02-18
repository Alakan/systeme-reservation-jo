package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.payload.request.LoginRequest;
import com.example.systeme_reservation_jo.payload.request.SignupRequest;
import com.example.systeme_reservation_jo.payload.response.JwtAuthenticationResponse;
import com.example.systeme_reservation_jo.payload.response.MessageResponse;
import com.example.systeme_reservation_jo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000") // A changer une fois le front créé
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtAuthenticationResponse jwtResponse = authService.login(loginRequest);
        return ResponseEntity.ok(jwtResponse); // Retourne le JWT avec un code 200 OK
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.register(signupRequest);
    }
}