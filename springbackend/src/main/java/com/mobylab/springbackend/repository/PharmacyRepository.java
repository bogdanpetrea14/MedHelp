package com.mobylab.springbackend.repository;

import com.mobylab.springbackend.entity.Pharmacy;
import com.mobylab.springbackend.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, UUID> {
    Optional<Pharmacy> findByUserEmail(String email);
    List<Pharmacy> findAllByUserStatus(UserStatus status);
}