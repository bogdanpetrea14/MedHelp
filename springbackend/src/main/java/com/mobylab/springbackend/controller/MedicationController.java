package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.dto.CreateMedicationDto;
import com.mobylab.springbackend.dto.MedicationResponseDto;
import com.mobylab.springbackend.service.MedicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;

    @GetMapping
    public ResponseEntity<List<MedicationResponseDto>> getAll() {
        return ResponseEntity.ok(medicationService.getAll());
    }

    @PostMapping
    public ResponseEntity<MedicationResponseDto> create(@Valid @RequestBody CreateMedicationDto dto) {
        return new ResponseEntity<>(medicationService.create(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        medicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}