package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.dto.CreatePrescriptionDto;
import com.mobylab.springbackend.entity.Prescription;
import com.mobylab.springbackend.enums.PrescriptionStatus;
import com.mobylab.springbackend.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    /**
     * Endpoint pentru Doctor: Crearea unei rețete noi
     * Aici se va declanșa și trimiterea mailului automat via MailTrap!
     */
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> createPrescription(@RequestBody CreatePrescriptionDto dto) {
        prescriptionService.createPrescription(dto);
        return new ResponseEntity<>("Rețeta a fost emisă cu succes și pacientul a fost notificat pe mail!", HttpStatus.CREATED);
    }

    /**
     * Endpoint pentru Pacient: Vizualizarea propriilor rețete
     */
    @GetMapping("/my-prescriptions")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<Prescription>> getMyPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsForCurrentPatient());
    }

    /**
     * Endpoint pentru Farmacie: Căutarea unei rețete după codul unic (ex: MED-A1B2C3D4)
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('PHARMACY')")
    public ResponseEntity<Prescription> searchByCode(@RequestParam String code) {
        return ResponseEntity.ok(prescriptionService.getByUniqueCode(code));
    }

    /**
     * Endpoint pentru Farmacie: Eliberarea (marcarea) rețetei
     * Folosim PATCH pentru că actualizăm doar un singur câmp (statusul)
     */
    @PatchMapping("/{code}/fulfill")
    @PreAuthorize("hasRole('PHARMACY')")
    public ResponseEntity<String> fulfillPrescription(
            @PathVariable String code,
            @RequestParam PrescriptionStatus status) {

        prescriptionService.updatePrescriptionStatus(code, status);
        return ResponseEntity.ok("Statusul rețetei " + code + " a fost actualizat cu succes la: " + status);
    }
}