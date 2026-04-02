package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.dto.PatientResponseDto;
import com.mobylab.springbackend.entity.Patient;
import com.mobylab.springbackend.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor // Lombok face injectarea Service-ului automat
public class PatientController {

    private final PatientService patientService;

    /**
     * Pasul 1 după înregistrare: Completarea datelor personale.
     * Doar un utilizator cu rolul PATIENT poate apela asta.
     */
    @PostMapping("/complete-profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<String> completeProfile(@RequestBody Patient patient) {
        patientService.createProfile(patient);
        return new ResponseEntity<>("Profilul de pacient a fost creat cu succes!", HttpStatus.CREATED);
    }

    /**
     * Endpoint pentru "Pagina Mea".
     * Backend-ul știe cine ești din Token, deci nu e nevoie de ID în URL.
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Patient> getMyProfile() {
        return ResponseEntity.ok(patientService.getMyProfile());
    }

    /**
     * Vizualizarea unui pacient specific.
     * Adminii și Doctorii pot vedea pe oricine.
     * Pacienții pot vedea doar dacă ID-ul le aparține (verificat în Service).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT', 'PHARMACY')")
    public ResponseEntity<Patient> getPatientById(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('DOCTOR')")
    public ResponseEntity<List<PatientResponseDto>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/my-patients")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<List<PatientResponseDto>> getMyPatients() {
        return ResponseEntity.ok(patientService.getMyPatients());
    }
}