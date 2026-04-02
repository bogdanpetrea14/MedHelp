package com.mobylab.springbackend.service;

import com.mobylab.springbackend.dto.CancelPrescriptionDto;
import com.mobylab.springbackend.dto.CreatePrescriptionDto;
import com.mobylab.springbackend.dto.FulfillDto;
import com.mobylab.springbackend.dto.UpdatePrescriptionDto;
import com.mobylab.springbackend.entity.*;
import com.mobylab.springbackend.enums.PrescriptionStatus;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.*;
import com.mobylab.springbackend.repository.PrescriptionCancellationRepository;
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
    private final PrescriptionCancellationRepository cancellationRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ActiveSubstanceRepository activeSubstanceRepository;
    private final EmailService emailService;
    private final PharmacyStockRepository stockRepository;
    private final PharmacyService pharmacyService;
    private final AllergyRepository allergyRepository;
    private final com.mobylab.springbackend.repository.UserRepository userRepository;

    @Transactional
    public void createPrescription(CreatePrescriptionDto dto) {
        // 1. Identificăm Doctorul logat
        String doctorEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                .orElseThrow(() -> new BadRequestException("Doar un medic cu profil complet poate prescrie!"));

        // 2. Identificăm Pacientul
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new BadRequestException("Pacientul specificat nu există!"));

        // --- 🛡️ LOGICA DE VERIFICARE ALERGII ---
        // Recuperăm toate alergiile cunoscute ale acestui pacient
        List<Allergy> patientAllergies = allergyRepository.findAllByPatientId(patient.getId());

        // Verificăm fiecare substanță din DTO înainte de a crea obiectele Entity
        for (var itemDto : dto.getItems()) {
            for (Allergy allergy : patientAllergies) {
                if (allergy.getActiveSubstance().getId().equals(itemDto.getActiveSubstanceId())) {
                    throw new BadRequestException("ATENȚIE: Pacientul este ALERGIC la "
                            + allergy.getActiveSubstance().getName()
                            + " (Severitate: " + allergy.getSeverity() + "). Rețeta nu a fost emisă!");
                }
            }
        }
        // ----------------------------------------

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

        // 6. Trimitere email la pacient (doar dacă nu s-a aruncat nicio eroare de alergie mai sus)
        emailService.sendPrescriptionEmail(
                patient.getUser().getEmail(),
                patient.getFirstName() + " " + patient.getLastName(),
                uniqueCode
        );
    }

    public List<Prescription> getPrescriptionsForCurrentPatient() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new BadRequestException("Profil de pacient inexistent!"));

        return prescriptionRepository.findAllByPatientId(patient.getId());
    }

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

    @Transactional
    public void fulfillPrescription(String uniqueCode, FulfillDto dto) {
        if (dto.getStatus() != PrescriptionStatus.FULFILLED && dto.getStatus() != PrescriptionStatus.PARTIALLY_FULFILLED) {
            throw new BadRequestException("Status invalid! O farmacie poate doar să elibereze (total sau parțial) o rețetă.");
        }

        Prescription prescription = prescriptionRepository.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new BadRequestException("Rețeta cu codul " + uniqueCode + " nu a fost găsită!"));

        if (prescription.getStatus() == PrescriptionStatus.FULFILLED) {
            throw new BadRequestException("Această rețetă a fost deja eliberată complet!");
        }
        if (prescription.getStatus() == PrescriptionStatus.CANCELLED) {
            throw new BadRequestException("Această rețetă a fost anulată de doctor!");
        }

        Pharmacy currentPharmacy = pharmacyService.getCurrentPharmacy();

        PharmacyStock stock = stockRepository.findByPharmacyIdAndMedicationId(currentPharmacy.getId(), dto.getMedicationId())
                .orElseThrow(() -> new BadRequestException("Acest medicament nu se află în stocul farmaciei tale!"));

        if (stock.getQuantity() < dto.getQuantity()) {
            throw new BadRequestException("Stoc insuficient! Mai ai doar " + stock.getQuantity() + " bucăți pe raft.");
        }

        stock.setQuantity(stock.getQuantity() - dto.getQuantity());
        stockRepository.save(stock);

        prescription.setStatus(dto.getStatus());
        prescriptionRepository.save(prescription);
    }

    public List<Prescription> getDoctorPrescriptions() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Doctor doctor = doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new BadRequestException("Profil de doctor inexistent!"));
        return prescriptionRepository.findAllByDoctorId(doctor.getId());
    }

    public void deletePrescription(UUID id) {
        prescriptionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Rețeta nu a fost găsită!"));
        prescriptionRepository.deleteById(id);
    }

    @Transactional
    public void cancelPrescription(UUID id, CancelPrescriptionDto dto) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Rețeta nu a fost găsită!"));

        if (prescription.getStatus() == PrescriptionStatus.CANCELLED) {
            throw new BadRequestException("Rețeta este deja anulată!");
        }
        if (prescription.getStatus() == PrescriptionStatus.FULFILLED) {
            throw new BadRequestException("Nu poți anula o rețetă deja eliberată!");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (!isAdmin && !prescription.getDoctor().getUser().getEmail().equals(email)) {
            throw new BadRequestException("Nu poți anula rețeta altui doctor!");
        }

        User cancelledBy = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new BadRequestException("Utilizatorul nu a fost găsit!"));

        prescription.setStatus(PrescriptionStatus.CANCELLED);
        prescriptionRepository.save(prescription);

        cancellationRepository.save(new PrescriptionCancellation()
                .setPrescription(prescription)
                .setCancelledBy(cancelledBy)
                .setReason(dto.getReason()));
    }

    public void updatePrescription(UUID id, UpdatePrescriptionDto dto) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Rețeta nu a fost găsită!"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (!isAdmin && !prescription.getDoctor().getUser().getEmail().equals(email)) {
            throw new BadRequestException("Nu poți modifica rețeta altui doctor!");
        }

        if (dto.getDoctorNotes() != null) {
            prescription.setDoctorNotes(dto.getDoctorNotes());
        }
        if (dto.getStatus() != null) {
            prescription.setStatus(dto.getStatus());
        }

        prescriptionRepository.save(prescription);
    }

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }
}