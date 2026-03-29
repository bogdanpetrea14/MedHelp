package com.mobylab.springbackend.service;

import com.mobylab.springbackend.dto.ActiveSubstanceResponseDto;
import com.mobylab.springbackend.dto.CreateActiveSubstanceDto;
import com.mobylab.springbackend.entity.ActiveSubstance;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.ActiveSubstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok generează constructorul pentru injection
public class ActiveSubstanceService {

    private final ActiveSubstanceRepository activeSubstanceRepository;

    // 1. Obținerea tuturor substanțelor (READ ALL)
    public List<ActiveSubstanceResponseDto> getAll() {
        return activeSubstanceRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 2. Obținerea unei singure substanțe (READ ONE)
    public ActiveSubstanceResponseDto getById(UUID id) {
        ActiveSubstance substance = activeSubstanceRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Substanța activă cu ID-ul specificat nu există!"));
        return mapToResponse(substance);
    }

    // 3. Crearea unei substanțe noi (CREATE)
    public ActiveSubstanceResponseDto create(CreateActiveSubstanceDto dto) {
        // Validăm dacă numele este deja folosit (pentru regula UNIQUE)
        if (activeSubstanceRepository.existsByName(dto.getName())) {
            throw new BadRequestException("O substanță cu numele '" + dto.getName() + "' există deja!");
        }

        ActiveSubstance substance = new ActiveSubstance()
                .setName(dto.getName())
                .setDescription(dto.getDescription())
                .setCategory(dto.getCategory());

        ActiveSubstance savedSubstance = activeSubstanceRepository.save(substance);
        return mapToResponse(savedSubstance);
    }

    // 4. Ștergerea (DELETE)
    public void delete(UUID id) {
        if (!activeSubstanceRepository.existsById(id)) {
            throw new BadRequestException("Nu am putut șterge: Substanța nu există.");
        }
        activeSubstanceRepository.deleteById(id);
    }

    // --- Helper: Transformă Entitatea în DTO (Mapping) ---
    private ActiveSubstanceResponseDto mapToResponse(ActiveSubstance entity) {
        return new ActiveSubstanceResponseDto()
                .setId(entity.getId())
                .setName(entity.getName())
                .setDescription(entity.getDescription())
                .setCategory(entity.getCategory());
    }
}