package com.example.backend.repositories;

import com.example.backend.models.logistique.Expedition;
import com.example.backend.models.operations.Annulation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnnulationRepository extends JpaRepository<Annulation, Integer> {
    Optional<Annulation> findByExpedition(Expedition expedition);
}
