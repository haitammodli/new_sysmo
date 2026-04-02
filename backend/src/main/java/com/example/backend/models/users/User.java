package com.example.backend.models.users;

import com.example.backend.enums.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "utilisateurs")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", length = 10)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_code_seq")
    @SequenceGenerator(name = "user_code_seq", sequenceName = "user_code_sequence", initialValue = 1000, allocationSize = 1)
    @Min(value = 1000, message = "Le code doit comporter entre 4 et 7 chiffres")
    @Max(value = 999999, message = "Le code doit comporter entre 4 et 7 chiffres")
    @Column(length = 50, nullable = false)
    private Long code;

    private String nom;
    private String prenom;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Roles role;
}
