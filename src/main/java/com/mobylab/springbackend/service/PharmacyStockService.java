package com.mobylab.springbackend.service;

import com.mobylab.springbackend.dto.PharmacyStockResponseDto;
import com.mobylab.springbackend.dto.UpdateStockDto;
import com.mobylab.springbackend.entity.Medication;
import com.mobylab.springbackend.entity.Pharmacy;
import com.mobylab.springbackend.entity.PharmacyStock;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.MedicationRepository;
import com.mobylab.springbackend.repository.PharmacyStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PharmacyStockService {

    private final PharmacyStockRepository stockRepository;
    private final PharmacyService pharmacyService;
    private final MedicationRepository medicationRepository;

    public void updateStock(UpdateStockDto dto) {
        Pharmacy currentPharmacy = pharmacyService.getCurrentPharmacy();

        Medication medication = medicationRepository.findById(dto.getMedicationId())
                .orElseThrow(() -> new BadRequestException("Medicamentul nu există în sistem!"));

        if (dto.getQuantity() <= 0) {
            throw new BadRequestException("Cantitatea adăugată trebuie să fie mai mare decât 0!");
        }

        // Căutăm dacă avem deja medicamentul pe stoc
        PharmacyStock stock = stockRepository.findByPharmacyIdAndMedicationId(currentPharmacy.getId(), medication.getId())
                .orElse(new PharmacyStock());

        if (stock.getId() == null) {
            // E un medicament nou pe raftul nostru
            stock.setPharmacy(currentPharmacy);
            stock.setMedication(medication);
            stock.setQuantity(dto.getQuantity());
        } else {
            // Există deja, adunăm stocul nou la cel vechi
            stock.setQuantity(stock.getQuantity() + dto.getQuantity());
        }

        // Indiferent dacă e nou sau vechi, actualizăm prețul la cel din DTO
        stock.setPrice(dto.getPrice());

        stockRepository.save(stock);
    }

    public List<PharmacyStockResponseDto> getMyStock() {
        Pharmacy currentPharmacy = pharmacyService.getCurrentPharmacy();

        // Luăm stocul din baza de date și îl transformăm în DTO-ul tău de Response
        return stockRepository.findAllByPharmacyId(currentPharmacy.getId())
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // Funcție de ajutor pentru a mapa Entitatea la DTO
    private PharmacyStockResponseDto mapToResponseDto(PharmacyStock stock) {
        return new PharmacyStockResponseDto()
                .setId(stock.getId())
                .setMedicationName(stock.getMedication().getBrandName())
                .setQuantity(stock.getQuantity())
                .setPrice(stock.getPrice());
    }
}