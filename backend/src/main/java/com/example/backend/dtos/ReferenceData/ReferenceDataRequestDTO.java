package com.example.backend.dtos.ReferenceData;

import lombok.Data;

@Data
public class ReferenceDataRequestDTO {
    private Long id;
    private String categorie;
    private String libelle;
    private boolean active;
}
