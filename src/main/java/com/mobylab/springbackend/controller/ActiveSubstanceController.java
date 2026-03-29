package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.dto.ActiveSubstanceResponseDto;
import com.mobylab.springbackend.dto.CreateActiveSubstanceDto;
import com.mobylab.springbackend.service.ActiveSubstanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/active-substances")
@RequiredArgsConstructor
public class ActiveSubstanceController {

    private final ActiveSubstanceService activeSubstanceService;

    @GetMapping
    public ResponseEntity<List<ActiveSubstanceResponseDto>> getAll() {
        return ResponseEntity.ok(activeSubstanceService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // <--- Doar Adminul are voie
    public ResponseEntity<ActiveSubstanceResponseDto> create(@Valid @RequestBody CreateActiveSubstanceDto dto) {
        return new ResponseEntity<>(activeSubstanceService.create(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <--- Doar Adminul are voie
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        activeSubstanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}