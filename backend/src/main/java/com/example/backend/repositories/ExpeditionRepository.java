package com.example.backend.repositories;

import com.example.backend.models.logistique.Expedition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpeditionRepository extends JpaRepository<Expedition, String> {
    Optional<Expedition> findByNumerodeclaration(Long numerodeclaration);
}
