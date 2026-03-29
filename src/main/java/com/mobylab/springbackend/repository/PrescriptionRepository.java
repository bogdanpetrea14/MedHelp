package com.mobylab.springbackend.repository;

import com.mobylab.springbackend.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    boolean existsByUniqueCode(String uniqueCode);

    // Găsește toate rețetele unui pacient folosind ID-ul lui
    List<Prescription> findAllByPatientId(UUID patientId);
    Optional<Prescription> findByUniqueCode(String uniqueCode);
}