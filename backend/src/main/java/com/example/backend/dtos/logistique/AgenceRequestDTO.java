package com.example.backend.dtos.logistique;

import lombok.Data;

@Data
public class AgenceRequestDTO {
    private Long code;
    private String nom;
    private String ville;
    private Long matricule;
}
