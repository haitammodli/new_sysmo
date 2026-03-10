package com.example.backend.models.systeme;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="reference")
public class ReferenceData {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String categorie;

    @Column(nullable = false, length = 100)
    private String libelle;

    private boolean actif = true;
}
