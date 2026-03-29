package com.mobylab.springbackend.repository;

import com.mobylab.springbackend.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, UUID> {
    Optional<Pharmacy> findByUserEmail(String email);
}