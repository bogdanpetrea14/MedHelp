package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.dto.CreatePrescriptionDto;
import com.mobylab.springbackend.dto.FulfillDto;
import com.mobylab.springbackend.entity.Prescription;
import com.mobylab.springbackend.service.PrescriptionService;
import jakarta.validation.Valid;
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
     */
    @PostMapping
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<String> createPrescription(@RequestBody CreatePrescriptionDto dto) {
        prescriptionService.createPrescription(dto);
        return new ResponseEntity<>("Rețeta a fost emisă cu succes și pacientul a fost notificat pe mail!", HttpStatus.CREATED);
    }

    /**
     * Endpoint pentru Pacient: Vizualizarea propriilor rețete
     */
    @GetMapping("/my-prescriptions")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<List<Prescription>> getMyPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsForCurrentPatient());
    }

    /**
     * Endpoint pentru Farmacie: Căutarea unei rețete după codul unic
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('PHARMACY')")
    public ResponseEntity<Prescription> searchByCode(@RequestParam String code) {
        return ResponseEntity.ok(prescriptionService.getByUniqueCode(code));
    }

    /**
     * Endpoint pentru Farmacie: Eliberarea rețetei (cu scădere de stoc)
     * Acum folosește @RequestBody FulfillDto în loc de RequestParam
     */
    @PatchMapping("/{code}/fulfill")
    @PreAuthorize("hasAuthority('PHARMACY')")
    public ResponseEntity<String> fulfillPrescription(
            @PathVariable String code,
            @Valid @RequestBody FulfillDto dto) {

        prescriptionService.fulfillPrescription(code, dto);
        return ResponseEntity.ok("Rețeta a fost actualizată, iar stocul farmaciei a fost redus cu succes!");
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
        // În service doar faci: return prescriptionRepository.findAll();
    }
}