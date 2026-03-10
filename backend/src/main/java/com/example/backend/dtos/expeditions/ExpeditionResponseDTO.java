package com.example.backend.dtos.expeditions;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExpeditionResponseDTO {
    private Long numeroexpedition;
    private LocalDateTime dateCreation;
    private String statut;

    // Echoing input for verification
    private double ht;
    private double tva;
    private double ttc;
    private Long expiditeurId;
    private Long distinataireId;
    private Long agenceId;
}
