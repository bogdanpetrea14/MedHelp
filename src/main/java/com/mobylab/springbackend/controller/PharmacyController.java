package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.entity.Pharmacy;
import com.mobylab.springbackend.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacies")
@RequiredArgsConstructor
public class PharmacyController {

    private final PharmacyService pharmacyService;

    /**
     * Endpoint pentru completarea profilului de farmacie.
     * Doar un utilizator cu rolul PHARMACY poate apela asta.
     */
    @PostMapping("/complete-profile")
    @PreAuthorize("hasRole('PHARMACY')")
    public ResponseEntity<String> completeProfile(@RequestBody Pharmacy pharmacy) {
        pharmacyService.createProfile(pharmacy);
        return new ResponseEntity<>("Profilul farmaciei a fost creat cu succes!", HttpStatus.CREATED);
    }

    /**
     * Endpoint pentru ca farmacia logată să-și vadă propriile date (program, adresă, etc.)
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('PHARMACY')")
    public ResponseEntity<Pharmacy> getMyProfile() {
        return ResponseEntity.ok(pharmacyService.getMyProfile());
    }

    /**
     * Endpoint util pentru Admin sau Pacienți (să vadă lista de farmacii disponibile).
     * Îl facem accesibil oricărui utilizator logat.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Pharmacy>> getAllPharmacies() {
        // Presupunând că adaugi o metodă simplă de findAll în Service
        return ResponseEntity.ok(pharmacyService.getAll());
    }
}