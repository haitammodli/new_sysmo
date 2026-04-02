package com.example.backend.dtos.logistique;

import lombok.Data;

@Data
public class AgenceResponseDTO {
    private Long code;
    private String nom;
    private String ville;

    //chef info
    private Long chefMatricule;
    private String chefNomComplet;
}
