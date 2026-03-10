package com.example.backend.repositories;

import com.example.backend.models.systeme.HistoriqueAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriqueAuditRepository extends JpaRepository<HistoriqueAudit, Integer> {
    List<HistoriqueAudit> findByEntiteIdOrderByDateActionDesc(Long entiteId);
}
