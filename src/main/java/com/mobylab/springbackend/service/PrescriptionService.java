package com.mobylab.springbackend.service;

import com.mobylab.springbackend.dto.CreatePrescriptionDto;
import com.mobylab.springbackend.entity.*;
import com.mobylab.springbackend.enums.PrescriptionStatus;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ActiveSubstanceRepository activeSubstanceRepository;
    private final EmailService emailService;

    @Transactional
    public void createPrescription(CreatePrescriptionDto dto) {
        // 1. Identificăm Doctorul logat
        String doctorEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                .orElseThrow(() -> new BadRequestException("Doar un medic cu profil complet poate prescrie!"));

        // 2. Identificăm Pacientul
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new BadRequestException("Pacientul specificat nu există!"));

        // 3. Generăm codul unic
        String uniqueCode = generateUniquePrescriptionCode();

        // 4. Construim Rețeta (Prescription)
        Prescription prescription = new Prescription()
                .setUniqueCode(uniqueCode)
                .setDoctor(doctor)
                .setPatient(patient)
                .setStatus(PrescriptionStatus.PRESCRIBED);

        // 5. Adăugăm elementele rețetei (PrescriptionItems) - MAPARE EXACTĂ PE DTO-UL TĂU
        List<PrescriptionItem> items = dto.getItems().stream().map(itemDto -> {
            ActiveSubstance substance = activeSubstanceRepository.findById(itemDto.getActiveSubstanceId())
                    .orElseThrow(() -> new BadRequestException("Substanța activă nu a fost găsită!"));

            return new PrescriptionItem()
                    .setPrescription(prescription)
                    .setActiveSubstance(substance)
                    .setDose(itemDto.getDose())            // Folosește getDose() din DTO
                    .setFrequency(itemDto.getFrequency()) // Folosește getFrequency() din DTO
                    .setDurationDays(itemDto.getDurationDays())
                    .setNotes(itemDto.getNotes());
        }).collect(Collectors.toList());

        prescription.setItems(items);
        prescriptionRepository.save(prescription);

        emailService.sendPrescriptionEmail(
                patient.getUser().getEmail(),
                patient.getFirstName() + " " + patient.getLastName(),
                uniqueCode
        );
    }

    /**
     * Returnează rețetele pacientului logat
     */
    public List<Prescription> getPrescriptionsForCurrentPatient() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new BadRequestException("Profil de pacient inexistent!"));

        return prescriptionRepository.findAllByPatientId(patient.getId());
    }

    /**
     * Căutare după cod (pentru Farmacie)
     */
    public Prescription getByUniqueCode(String code) {
        return prescriptionRepository.findByUniqueCode(code)
                .orElseThrow(() -> new BadRequestException("Rețeta cu codul " + code + " nu a fost găsită!"));
    }

    private String generateUniquePrescriptionCode() {
        String code;
        do {
            code = "MED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (prescriptionRepository.existsByUniqueCode(code));
        return code;
    }

    /**
     * Logica de eliberare a rețetei (Farmacie)
     */
    @Transactional
    public void updatePrescriptionStatus(String uniqueCode, PrescriptionStatus newStatus) {
        // 1. Căutăm rețeta
        Prescription prescription = prescriptionRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new BadRequestException("Rețeta cu codul " + uniqueCode + " nu a fost găsită!"));

        // 2. Verificăm să nu fie deja eliberată complet
        if (prescription.getStatus() == PrescriptionStatus.FULFILLED) {
            throw new BadRequestException("Această rețetă a fost deja onorată integral și nu mai poate fi refolosită!");
        }

        // 3. Verificăm ca noul status să aibă sens (Farmacia poate da doar FULFILLED sau PARTIALLY_FULFILLED)
        if (newStatus != PrescriptionStatus.FULFILLED && newStatus != PrescriptionStatus.PARTIALLY_FULFILLED) {
            throw new BadRequestException("Status invalid pentru eliberarea din farmacie!");
        }

        // 4. Actualizăm și salvăm
        prescription.setStatus(newStatus);
        prescriptionRepository.save(prescription);
    }
}