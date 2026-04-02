package com.example.backend.repositories;

import com.example.backend.models.logistique.Agence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgenceRepository extends JpaRepository<Agence, Long> {
    List<Agence> findByNomContainingIgnoreCaseOrVilleContainingIgnoreCase(String nom, String ville);
}
