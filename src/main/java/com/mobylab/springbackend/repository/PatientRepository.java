package com.mobylab.springbackend.repository;

import com.mobylab.springbackend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByCnp(String cnp);

    // ACEASTA ESTE MAGIA: Spring va intra în entitatea User și va căuta după email
    Optional<Patient> findByUserEmail(String email);
}