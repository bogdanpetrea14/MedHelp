package com.mobylab.springbackend.service;

import com.mobylab.springbackend.entity.Patient;
import com.mobylab.springbackend.entity.Pharmacy;
import com.mobylab.springbackend.entity.User;
import com.mobylab.springbackend.enums.UserRole;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.PatientRepository;
import com.mobylab.springbackend.repository.PharmacyRepository;
import com.mobylab.springbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createProfile(Pharmacy pharmacyData) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByEmail(email).orElseThrow();

        if (user.getRole() != UserRole.PHARMACY) {
            throw new BadRequestException("Acest cont nu are dreptul de a administra o farmacie!");
        }

        if (pharmacyRepository.findByUserEmail(email).isPresent()) {
            throw new BadRequestException("Profilul farmaciei există deja!");
        }

        pharmacyData.setUser(user);
        pharmacyRepository.save(pharmacyData);
    }

    public Pharmacy getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return pharmacyRepository.findByUserEmail(email)
                .orElseThrow(() -> new BadRequestException("Profil de farmacie inexistent!"));
    }

    public List<Pharmacy> getAll() {
        return pharmacyRepository.findAll();
    }
}