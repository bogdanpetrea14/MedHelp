package com.mobylab.springbackend.repository;

import com.mobylab.springbackend.entity.ActiveSubstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ActiveSubstanceRepository extends JpaRepository<ActiveSubstance, UUID> {
    // Putem adăuga căutare după nume dacă avem nevoie:
    boolean existsByName(String name);
}