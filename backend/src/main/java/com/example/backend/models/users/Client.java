package com.example.backend.models.users;

import com.example.backend.enums.TypeClient;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("CLIENT")
public class Client extends User{

    private String client;
    private String adresse;
    private String telephone;
    private String contact;
    private String secteur;

    @Enumerated(EnumType.STRING)
    private TypeClient typeClient;

    @Column(name = "liste_noire")
    private Boolean listeNoire;

    @Column(name = "motif_liste_noire", length = 255)
    private String motifListeNoire;
}
