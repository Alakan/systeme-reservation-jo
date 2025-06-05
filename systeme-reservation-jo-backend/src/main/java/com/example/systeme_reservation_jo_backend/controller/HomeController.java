package com.example.systeme_reservation_jo_backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST pour gérer la page d'accueil de l'API.
 */
@RestController
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * Endpoint pour afficher un message de bienvenue.
     *
     * @return un message de bienvenue
     */
    @GetMapping("/")
    public String home() {
        logger.info("Accès à la page d'accueil");
        return "Bienvenue sur l'API du système de réservation";
    }
}
