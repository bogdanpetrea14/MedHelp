package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.dto.AllergyResponseDto;
import com.mobylab.springbackend.dto.CreateAllergyDto;
import com.mobylab.springbackend.service.AllergyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/allergies")
@RequiredArgsConstructor
public class AllergyController {

    private final AllergyService allergyService;

    @PostMapping
    @PreAuthorize("hasAuthority('PATIENT') or hasAuthority('DOCTOR')")
    public ResponseEntity<String> createAllergy(@Valid @RequestBody CreateAllergyDto dto) {
        allergyService.createAllergy(dto);
        return ResponseEntity.ok("Alergia a fost înregistrată cu succes!");
    }
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('PATIENT') or hasAuthority('DOCTOR')")
    public ResponseEntity<List<AllergyResponseDto>> getPatientAllergies(@PathVariable UUID patientId) {
        return ResponseEntity.ok(allergyService.getPatientAllergies(patientId));
    }
}