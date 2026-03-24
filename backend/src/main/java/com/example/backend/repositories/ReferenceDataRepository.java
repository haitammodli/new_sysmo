package com.example.backend.repositories;

import com.example.backend.models.systeme.ReferenceData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferenceDataRepository extends JpaRepository<ReferenceData, Long> {
    List<ReferenceData> findByCategorieAndActifTrue(String categorie);

    List<ReferenceData> findByCategorie(String categorie);
}
