package com.example.backend.dtos.auth;

import lombok.Data;
import java.util.List;

@Data
public class RegisterRequestDTO {
    private Long code;
    private String email;
    private String password;
    private String nom;
    private String prenom;
    private String userType;

    // Optional fields for Client
    private String client;
    private String adresse;
    private String telephone;
    private String contact;
    private String secteur;
    private String typeClient;

    // Optional fields for ChefAgence
    private List<Long> agenceIds;
}
