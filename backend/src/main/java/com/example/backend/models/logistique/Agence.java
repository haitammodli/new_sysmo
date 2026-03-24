package com.example.backend.models.logistique;

import com.example.backend.models.users.ChefAgence;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor  @AllArgsConstructor
@Table(name = "agence destinataire")
public class Agence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chef_id")
    private ChefAgence chef;
    private String nom;
    private String ville;

}
