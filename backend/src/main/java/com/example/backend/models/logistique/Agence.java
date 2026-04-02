package com.example.backend.models.logistique;

import com.example.backend.models.users.ChefAgence;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor  @AllArgsConstructor
@Table(name = "agence_destinataire")
public class Agence {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agence_code_seq")
    @SequenceGenerator(name = "agence_code_seq", sequenceName = "agence_code_sequence", initialValue = 100, allocationSize = 1)
    @Min(value = 100, message = "Le code doit comporter 3 chiffres")
    @Max(value = 999, message = "Le code doit comporter 3 chiffres")
    @Column(length = 50, nullable = false)
    private Long code;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="matricule")
    private ChefAgence chef;
    private String nom;
    private String ville;

}
