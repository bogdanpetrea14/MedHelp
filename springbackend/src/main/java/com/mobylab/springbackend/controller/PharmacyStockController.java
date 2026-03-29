package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.dto.PharmacyStockResponseDto;
import com.mobylab.springbackend.dto.UpdateStockDto;
import com.mobylab.springbackend.service.PharmacyStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class PharmacyStockController {

    private final PharmacyStockService stockService;

    /**
     * Endpoint pentru adăugarea/actualizarea stocului unui medicament
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('PHARMACY')")
    public ResponseEntity<String> updateStock(@Valid @RequestBody UpdateStockDto dto) {
        stockService.updateStock(dto);
        return ResponseEntity.ok("Stocul și prețul au fost actualizate cu succes!");
    }

    /**
     * Endpoint pentru a vedea tot inventarul farmaciei logate
     */
    @GetMapping("/my-stock")
    @PreAuthorize("hasAuthority('PHARMACY')")
    public ResponseEntity<List<PharmacyStockResponseDto>> getMyStock() {
        return ResponseEntity.ok(stockService.getMyStock());
    }
}