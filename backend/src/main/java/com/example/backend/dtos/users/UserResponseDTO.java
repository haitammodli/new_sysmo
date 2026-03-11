package com.example.backend.dtos.users;

import com.example.backend.enums.Roles;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long code;
    private String nom;
    private String prenom;
    private String email;
    private Roles role;
    
    // Optional Client fields
    private String client;
    private String adresse;
    private String telephone;
    private String contact;
    private String secteur;
    private String typeClient;
    private Boolean listeNoire;
    private String motifListeNoire;
    
    // Additional fields could be mapped as needed
}
