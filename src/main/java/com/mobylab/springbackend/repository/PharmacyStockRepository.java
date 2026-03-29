package com.mobylab.springbackend.repository;

import com.mobylab.springbackend.entity.PharmacyStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PharmacyStockRepository extends JpaRepository<PharmacyStock, UUID> {

    // 1. Găsește tot inventarul (lista de medicamente) pentru o anumită farmacie
    List<PharmacyStock> findAllByPharmacyId(UUID pharmacyId);

    // 2. Caută să vadă dacă farmacia are DEJA un anumit medicament pe raft
    // Folosim asta ca să știm dacă facem UPDATE la cantitate sau CREATE la o înregistrare nouă
    Optional<PharmacyStock> findByPharmacyIdAndMedicationId(UUID pharmacyId, UUID medicationId);
}