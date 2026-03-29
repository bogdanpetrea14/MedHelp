package com.mobylab.springbackend.service;

import com.mobylab.springbackend.dto.CreatePrescriptionDto;
import com.mobylab.springbackend.dto.FulfillDto;
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

    // --- NOILE DEPENDINȚE PENTRU STOCUL FARMACIEI ---
    private final PharmacyStockRepository stockRepository;
    private final PharmacyService pharmacyService;

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

        // 5. Adăugăm elementele rețetei (PrescriptionItems)
        List<PrescriptionItem> items = dto.getItems().stream().map(itemDto -> {
            ActiveSubstance substance = activeSubstanceRepository.findById(itemDto.getActiveSubstanceId())
                    .orElseThrow(() -> new BadRequestException("Substanța activă nu a fost găsită!"));

            return new PrescriptionItem()
                    .setPrescription(prescription)
                    .setActiveSubstance(substance)
                    .setDose(itemDto.getDose())
                    .setFrequency(itemDto.getFrequency())
                    .setDurationDays(itemDto.getDurationDays())
                    .setNotes(itemDto.getNotes());
        }).collect(Collectors.toList());

        prescription.setItems(items);
        prescriptionRepository.save(prescription);

        // Trimitere email la pacient
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
     * NOUA LOGICĂ DE ELIBERARE (Care scade și stocul!)
     * (Înlocuiește vechea metodă updatePrescriptionStatus)
     */
    @Transactional
    public void fulfillPrescription(String uniqueCode, FulfillDto dto) {

        // 1. Validăm statusul (O farmacie poate doar să dea medicamente, nu să anuleze rețete)
        if (dto.getStatus() != PrescriptionStatus.FULFILLED && dto.getStatus() != PrescriptionStatus.PARTIALLY_FULFILLED) {
            throw new BadRequestException("Status invalid! O farmacie poate doar să elibereze (total sau parțial) o rețetă.");
        }

        // 2. Găsim rețeta după cod
        Prescription prescription = prescriptionRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new BadRequestException("Rețeta cu codul " + uniqueCode + " nu a fost găsită!"));

        // 3. Verificăm dacă mai poate fi folosită
        if (prescription.getStatus() == PrescriptionStatus.FULFILLED) {
            throw new BadRequestException("Această rețetă a fost deja eliberată complet!");
        }
        if (prescription.getStatus() == PrescriptionStatus.CANCELLED) {
            throw new BadRequestException("Această rețetă a fost anulată de doctor!");
        }

        // 4. Aflăm cine e farmacia care face cererea
        Pharmacy currentPharmacy = pharmacyService.getCurrentPharmacy();

        // 5. Căutăm medicamentul exact pe raftul farmaciei
        PharmacyStock stock = stockRepository.findByPharmacyIdAndMedicationId(currentPharmacy.getId(), dto.getMedicationId())
                .orElseThrow(() -> new BadRequestException("Acest medicament nu se află în stocul farmaciei tale!"));

        // 6. Verificăm dacă avem suficiente cutii pe raft
        if (stock.getQuantity() < dto.getQuantity()) {
            throw new BadRequestException("Stoc insuficient! Mai ai doar " + stock.getQuantity() + " bucăți pe raft.");
        }

        // 7. SCĂDEM STOCUL DE PE RAFT
        stock.setQuantity(stock.getQuantity() - dto.getQuantity());
        stockRepository.save(stock); // Salvăm noua cantitate

        // 8. ACTUALIZĂM STAREA REȚETEI
        prescription.setStatus(dto.getStatus());
        prescriptionRepository.save(prescription);
    }
}