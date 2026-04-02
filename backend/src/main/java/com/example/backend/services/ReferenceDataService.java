package com.example.backend.services;

import com.example.backend.dtos.ReferenceData.ReferenceDataRequestDTO;
import com.example.backend.dtos.ReferenceData.ReferenceDataResponseDTO;
import com.example.backend.models.systeme.ReferenceData;
import com.example.backend.repositories.ReferenceDataRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReferenceDataService {
   @Autowired
   private ReferenceDataRepository referenceDataRepository;

@Transactional
   public ReferenceDataResponseDTO save(ReferenceDataRequestDTO dto) {
       ReferenceData referenceData = new ReferenceData();
       if(dto.getCategorie()!=null) {
           referenceData.setCategorie(dto.getCategorie().toUpperCase());
       }
       referenceData.setLibelle(dto.getLibelle());
       referenceData.setActif(true);
       ReferenceData savedreferencedata = referenceDataRepository.save(referenceData);

       return maptoDTO(savedreferencedata);
    }
    public List<ReferenceDataResponseDTO> getAll() {
       return referenceDataRepository.findAll().stream()
               .map(this::maptoDTO).collect(Collectors.toList());
    }

//    public boolean delete(Long id) {
//       if(!referenceDataRepository.existsById(id)) {
//           return false;
//       }
//       referenceDataRepository.deleteById(id);
//       return true;
//    }
    @Transactional
    public ReferenceDataResponseDTO toggleStatus(Long id) {
        ReferenceData ref = referenceDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reference not found with id: " + id));

        ref.setActif(!ref.isActif());
        ReferenceData updatedEntity = referenceDataRepository.save(ref);

        return maptoDTO(updatedEntity);
    }

    public ReferenceDataResponseDTO getById(Long id) {
       ReferenceData referenceData = referenceDataRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("reference non trouve" + id));
       return maptoDTO(referenceData);
    }

    public List<ReferenceDataResponseDTO> getByCategory(String category) {
        return referenceDataRepository.findByCategorie(category.toUpperCase())
                .stream()
                .map(this::maptoDTO).collect(Collectors.toList());
    }
    public List<ReferenceDataResponseDTO> getActiveByCategory(String category) {
        return referenceDataRepository.findByCategorieAndActifTrue(category.toUpperCase())
                .stream()
                .map(this::maptoDTO).collect(Collectors.toList());
    }
    public ReferenceDataResponseDTO maptoDTO(ReferenceData referenceData) {
        if (referenceData == null) {
            return null;
        }
       ReferenceDataResponseDTO dto = new ReferenceDataResponseDTO();
       dto.setId(referenceData.getId());
       dto.setCategorie(referenceData.getCategorie());
       dto.setLibelle(referenceData.getLibelle());
       dto.setActive(referenceData.isActif());
        return dto;
    }
}
