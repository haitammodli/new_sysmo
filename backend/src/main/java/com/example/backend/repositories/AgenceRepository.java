package com.example.backend.repositories;

import com.example.backend.models.logistique.Agence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgenceRepository extends JpaRepository<Agence, Long> {
}
