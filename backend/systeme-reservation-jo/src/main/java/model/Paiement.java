package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double montant;

    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement;

    private LocalDateTime datePaiement;

    @OneToOne(mappedBy = "paiement")
    private Reservation reservation;

    // Constructeurs, getters et setters
}