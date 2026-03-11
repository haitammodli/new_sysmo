package com.example.backend.dtos.auth;

import lombok.Data;

@Data
public class RegisterRequestDTO {
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
    private Long agenceId;
}
