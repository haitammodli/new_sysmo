package com.example.backend.dtos.expeditions;

import lombok.Data;

@Data
public class ExpeditionRequestDTO {
    // Taxation Info
    private double poid;
    private double volume;
    private double ht;
    private double tva;

    // Relationships
    private Long expiditeurId;
    private Long distinataireId;
    private Long agenceId;
    private Long natureId;
    private Long typeId;
}
