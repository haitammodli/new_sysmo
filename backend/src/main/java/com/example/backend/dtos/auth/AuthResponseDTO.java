package com.example.backend.dtos.auth;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private String type = "Bearer";
    private Long code;
    private String nom;
    private String prenom;
    private String email;
    private String role;
}
