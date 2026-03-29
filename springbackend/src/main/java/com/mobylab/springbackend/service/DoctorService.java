package com.mobylab.springbackend.service;

import com.mobylab.springbackend.dto.CreateMedicationDto;
import com.mobylab.springbackend.dto.MedicationResponseDto;
import com.mobylab.springbackend.entity.ActiveSubstance;
import com.mobylab.springbackend.entity.Doctor;
import com.mobylab.springbackend.entity.Medication;
import com.mobylab.springbackend.entity.User;
import com.mobylab.springbackend.enums.UserRole;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.ActiveSubstanceRepository;
import com.mobylab.springbackend.repository.DoctorRepository;
import com.mobylab.springbackend.repository.MedicationRepository;
import com.mobylab.springbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createProfile(Doctor doctorData) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByEmail(email).orElseThrow();

        if (user.getRole() != UserRole.DOCTOR) {
            throw new BadRequestException("Doar utilizatorii cu rol de DOCTOR pot avea un profil de medic!");
        }

        if (doctorRepository.findByUserEmail(email).isPresent()) {
            throw new BadRequestException("Profilul de medic există deja!");
        }

        doctorData.setUser(user);
        doctorRepository.save(doctorData);
    }

    public Doctor getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new BadRequestException("Profil de medic inexistent!"));
    }
}
