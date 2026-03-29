package com.mobylab.springbackend.service;

import com.mobylab.springbackend.dto.AllergyResponseDto;
import com.mobylab.springbackend.dto.CreateAllergyDto;
import com.mobylab.springbackend.entity.ActiveSubstance;
import com.mobylab.springbackend.entity.Allergy;
import com.mobylab.springbackend.entity.Patient;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.ActiveSubstanceRepository;
import com.mobylab.springbackend.repository.AllergyRepository;
import com.mobylab.springbackend.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllergyService {

    private final AllergyRepository allergyRepository;
    private final PatientRepository patientRepository;
    private final ActiveSubstanceRepository activeSubstanceRepository;

    public void createAllergy(CreateAllergyDto dto) {
        // 1. Verificăm dacă pacientul există
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new BadRequestException("Pacientul nu există!"));

        // 2. Verificăm dacă substanța activă există
        ActiveSubstance substance = activeSubstanceRepository.findById(dto.getActiveSubstanceId())
                .orElseThrow(() -> new BadRequestException("Substanța activă nu există!"));

        // 3. Verificăm să nu aibă deja această alergie înregistrată
        if (allergyRepository.existsByPatientIdAndActiveSubstanceId(patient.getId(), substance.getId())) {
            throw new BadRequestException("Această alergie este deja înregistrată pentru acest pacient!");
        }

        // 4. Salvăm alergia
        Allergy allergy = new Allergy()
                .setPatient(patient)
                .setActiveSubstance(substance)
                .setSeverity(dto.getSeverity())
                .setNotes(dto.getNotes());

        allergyRepository.save(allergy);
    }

    public List<AllergyResponseDto> getPatientAllergies(UUID patientId) {
        return allergyRepository.findAllByPatientId(patientId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private AllergyResponseDto mapToResponseDto(Allergy allergy) {
        return new AllergyResponseDto()
                .setId(allergy.getId())
                .setActiveSubstanceName(allergy.getActiveSubstance().getName()) // Verifică dacă e .getName() sau altceva la tine
                .setSeverity(allergy.getSeverity())
                .setNotes(allergy.getNotes());
    }
}