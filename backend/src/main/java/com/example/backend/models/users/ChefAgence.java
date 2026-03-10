package com.example.backend.models.users;

import com.example.backend.models.logistique.Agence;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("CHEFAGENCE")
public class ChefAgence extends User {

    @OneToMany(mappedBy = "chef")
    private List<Agence> agences;
}
