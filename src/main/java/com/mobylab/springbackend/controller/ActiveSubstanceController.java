package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.dto.ActiveSubstanceResponseDto;
import com.mobylab.springbackend.dto.CreateActiveSubstanceDto;
import com.mobylab.springbackend.service.ActiveSubstanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/active-substances") // URL-ul de bază
@RequiredArgsConstructor
public class ActiveSubstanceController {

    private final ActiveSubstanceService activeSubstanceService;

    @GetMapping
    public ResponseEntity<List<ActiveSubstanceResponseDto>> getAll() {
        return ResponseEntity.ok(activeSubstanceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActiveSubstanceResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(activeSubstanceService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ActiveSubstanceResponseDto> create(@Valid @RequestBody CreateActiveSubstanceDto dto) {
        return new ResponseEntity<>(activeSubstanceService.create(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        activeSubstanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}