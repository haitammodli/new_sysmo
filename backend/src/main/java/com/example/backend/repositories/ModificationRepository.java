package com.example.backend.repositories;

import com.example.backend.enums.StatutModificationAnn;
import com.example.backend.models.logistique.Expedition;
import com.example.backend.models.operations.Modification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModificationRepository extends JpaRepository<Modification, Long> {
    List<Modification> findByNumeroExpedition(String numeroExpedition);

    List<Modification> findByNumeroExpeditionIn(List<String> numerosExpedition);

    long countByNumeroExpedition(String numeroExpedition);

    List<Modification> findByStatut(StatutModificationAnn statut);
}
