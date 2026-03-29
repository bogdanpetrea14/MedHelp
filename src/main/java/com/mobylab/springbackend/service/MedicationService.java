package com.mobylab.springbackend.service;

import com.mobylab.springbackend.dto.CreateMedicationDto;
import com.mobylab.springbackend.dto.MedicationResponseDto;
import com.mobylab.springbackend.entity.ActiveSubstance;
import com.mobylab.springbackend.entity.Medication;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.ActiveSubstanceRepository;
import com.mobylab.springbackend.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // Asigură integritatea bazei de date
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final ActiveSubstanceRepository activeSubstanceRepository;

    public List<MedicationResponseDto> getAll() {
        return medicationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public MedicationResponseDto create(CreateMedicationDto dto) {
        // 1. Verificăm dacă substanța activă chiar există în baza de date
        ActiveSubstance substance = activeSubstanceRepository.findById(dto.getActiveSubstanceId())
                .orElseThrow(() -> new BadRequestException("Substanța activă selectată nu există!"));

        // 2. Mapăm DTO-ul către Entitate
        Medication medication = new Medication()
                .setBrandName(dto.getBrandName())
                .setConcentration(dto.getConcentration())
                .setForm(dto.getForm())
                .setActiveSubstance(substance); // Setăm legătura (Many-to-One)

        // 3. Salvăm
        Medication saved = medicationRepository.save(medication);
        return mapToResponse(saved);
    }

    public void delete(UUID id) {
        if (!medicationRepository.existsById(id)) {
            throw new BadRequestException("Medicamentul nu a fost găsit.");
        }
        medicationRepository.deleteById(id);
    }

    private MedicationResponseDto mapToResponse(Medication entity) {
        return new MedicationResponseDto()
                .setId(entity.getId())
                .setBrandName(entity.getBrandName())
                .setConcentration(entity.getConcentration())
                .setForm(entity.getForm())
                .setActiveSubstanceId(entity.getActiveSubstance().getId())
                .setActiveSubstanceName(entity.getActiveSubstance().getName());
    }
}