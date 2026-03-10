package com.example.backend.models.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter @NoArgsConstructor
@DiscriminatorValue("RESPONSABLEMODIFICATION")
public class ResponsableModification extends User {

}
