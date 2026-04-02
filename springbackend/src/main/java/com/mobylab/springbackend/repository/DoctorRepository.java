package com.mobylab.springbackend.repository;

import com.mobylab.springbackend.entity.Doctor;
import com.mobylab.springbackend.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    Optional<Doctor> findByUserEmail(String email);
    List<Doctor> findAllByUserStatus(UserStatus status);
}