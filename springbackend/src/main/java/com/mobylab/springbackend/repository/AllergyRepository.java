package com.mobylab.springbackend.repository;

import com.mobylab.springbackend.entity.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, UUID> {

    // Luăm lista de alergii a pacientului pentru a o verifica la prescriere
    List<Allergy> findAllByPatientId(UUID patientId);

    // Prevenim adăugarea aceleiași alergii de mai multe ori
    boolean existsByPatientIdAndActiveSubstanceId(UUID patientId, UUID activeSubstanceId);
}