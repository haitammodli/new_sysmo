package com.example.backend.repositories;

import com.example.backend.models.logistique.Expedition;
import com.example.backend.models.operations.Modification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModificationRepository extends JpaRepository<Modification, Integer> {
    List<Modification> findByExpedition(Expedition expedition);
    long countByExpedition(Expedition expedition);
}
