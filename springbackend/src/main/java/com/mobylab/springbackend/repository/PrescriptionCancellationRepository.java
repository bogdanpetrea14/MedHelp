package com.mobylab.springbackend.repository;

import com.mobylab.springbackend.entity.PrescriptionCancellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrescriptionCancellationRepository extends JpaRepository<PrescriptionCancellation, UUID> {
    Optional<PrescriptionCancellation> findByPrescriptionId(UUID prescriptionId);
}