package com.example.systeme_reservation_jo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le paiement doit être associé à une réservation")
    @OneToOne(fetch = FetchType.LAZY) // Relation OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @NotNull(message = "Le montant ne peut pas être nul")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être supérieur à 0")
    @Column(nullable = false)
    private BigDecimal montant;

    @NotNull(message = "La date de paiement ne peut pas être nulle")
    @Column(nullable = false)
    private LocalDateTime datePaiement;

    @NotBlank(message = "La méthode de paiement ne peut pas être vide")
    @Column(nullable = false)
    private String methodePaiement;

    private String transactionId; // Optionnel, peut être null

    @NotBlank(message = "Le statut du paiement ne peut pas être vide")
    @Column(nullable = false)
    private String statut; // "SUCCES", "ECHEC", "EN_ATTENTE"
}