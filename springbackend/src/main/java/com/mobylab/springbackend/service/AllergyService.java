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
import com.mobylab.springbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;



@Service
@RequiredArgsConstructor
public class AllergyService {

    private final AllergyRepository allergyRepository;
    private final PatientRepository patientRepository;
    private final ActiveSubstanceRepository activeSubstanceRepository;

    public void createAllergy(CreateAllergyDto dto) {
        // Luam pacientul din token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new BadRequestException("Profil de pacient inexistent!"));

        ActiveSubstance substance = activeSubstanceRepository.findById(dto.getActiveSubstanceId())
                .orElseThrow(() -> new BadRequestException("Substanța activă nu există!"));

        if (allergyRepository.existsByPatientIdAndActiveSubstanceId(patient.getId(), substance.getId())) {
            throw new BadRequestException("Această alergie este deja înregistrată pentru acest pacient!");
        }

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
                .setActiveSubstanceId(allergy.getActiveSubstance().getId())
                .setActiveSubstanceName(allergy.getActiveSubstance().getName())
                .setSeverity(allergy.getSeverity())
                .setNotes(allergy.getNotes())
                .setPatientName(allergy.getPatient().getFirstName() + " " + allergy.getPatient().getLastName());
    }

    public void updateAllergy(UUID id, CreateAllergyDto dto) {
        Allergy allergy = allergyRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Alergia nu a fost găsită!"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));

        if (!isAdmin && !allergy.getPatient().getUser().getEmail().equals(email)) {
            throw new BadRequestException("Nu poți modifica alergia altui pacient!");
        }

        allergy.setSeverity(dto.getSeverity()).setNotes(dto.getNotes());
        allergyRepository.save(allergy);
    }

    public List<AllergyResponseDto> getAllergiesForCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));

        if (isAdmin) {
            return allergyRepository.findAll()
                    .stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());
        }

        // e PATIENT — returnăm doar ale lui
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new BadRequestException("Profil de pacient inexistent!"));

        return allergyRepository.findAllByPatientId(patient.getId())
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
}