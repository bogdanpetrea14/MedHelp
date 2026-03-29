package com.mobylab.springbackend.service;

import com.mobylab.springbackend.dto.CreateActiveSubstanceDto;
import com.mobylab.springbackend.dto.ActiveSubstanceResponseDto;
import com.mobylab.springbackend.entity.ActiveSubstance;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.ActiveSubstanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActiveSubstanceServiceTest {

    @Mock
    private ActiveSubstanceRepository activeSubstanceRepository;

    @InjectMocks
    private ActiveSubstanceService activeSubstanceService;

    private CreateActiveSubstanceDto createDto;

    @BeforeEach
    void setUp() {
        createDto = new CreateActiveSubstanceDto()
                .setName("Paracetamol")
                .setCategory("Analgezic")
                .setDescription("Pentru febra");
    }

    @Test
    void create_WhenNameExists_ShouldThrowBadRequestException() {
        // GIVEN: Simulăm că numele există deja în baza de date
        when(activeSubstanceRepository.existsByName("Paracetamol")).thenReturn(true);

        // WHEN & THEN: Verificăm că aruncă eroarea corectă
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            activeSubstanceService.create(createDto);
        });

        assertEquals("O substanță cu numele 'Paracetamol' există deja!", exception.getMessage());

        // Verificăm că metoda save() NU a fost apelată niciodată
        verify(activeSubstanceRepository, never()).save(any());
    }

    @Test
    void create_WhenNameIsNew_ShouldSaveSuccessfully() {
        // GIVEN: Numele nu există
        when(activeSubstanceRepository.existsByName("Paracetamol")).thenReturn(false);

        ActiveSubstance savedEntity = new ActiveSubstance()
                .setName("Paracetamol")
                .setCategory("Analgezic");

        when(activeSubstanceRepository.save(any(ActiveSubstance.class))).thenReturn(savedEntity);

        // WHEN: Apelăm metoda de creare
        ActiveSubstanceResponseDto result = activeSubstanceService.create(createDto);

        // THEN: Verificăm rezultatul
        assertNotNull(result);
        assertEquals("Paracetamol", result.getName());

        // Verificăm că save() a fost apelat exact o dată
        verify(activeSubstanceRepository, times(1)).save(any());
    }
}