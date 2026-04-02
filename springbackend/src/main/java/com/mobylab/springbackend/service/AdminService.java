package com.mobylab.springbackend.service;

import com.mobylab.springbackend.dto.*;
import com.mobylab.springbackend.entity.User;
import com.mobylab.springbackend.enums.UserStatus;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PharmacyRepository pharmacyRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    // ---- Liste utilizatori per tip ----

    public List<AdminPatientDto> getAllPatients() {
        return patientRepository.findAll().stream().map(p -> new AdminPatientDto()
                .setUserId(p.getUser().getId())
                .setProfileId(p.getId())
                .setEmail(p.getUser().getEmail())
                .setStatus(p.getUser().getStatus())
                .setFirstName(p.getFirstName())
                .setLastName(p.getLastName())
                .setCnp(p.getCnp())
                .setBirthDate(p.getBirthDate())
                .setPrescriptionCount(prescriptionRepository.countByPatientId(p.getId()))
        ).collect(Collectors.toList());
    }

    public List<AdminDoctorDto> getAllDoctors() {
        return doctorRepository.findAll().stream().map(this::mapDoctor).collect(Collectors.toList());
    }

    public List<AdminPharmacyDto> getAllPharmacies() {
        return pharmacyRepository.findAll().stream().map(this::mapPharmacy).collect(Collectors.toList());
    }

    public List<AdminDoctorDto> getPendingDoctors() {
        return doctorRepository.findAllByUserStatus(UserStatus.PENDING).stream().map(this::mapDoctor).collect(Collectors.toList());
    }

    public List<AdminPharmacyDto> getPendingPharmacies() {
        return pharmacyRepository.findAllByUserStatus(UserStatus.PENDING).stream().map(this::mapPharmacy).collect(Collectors.toList());
    }

    private AdminDoctorDto mapDoctor(com.mobylab.springbackend.entity.Doctor d) {
        return new AdminDoctorDto()
                .setUserId(d.getUser().getId())
                .setProfileId(d.getId())
                .setEmail(d.getUser().getEmail())
                .setStatus(d.getUser().getStatus())
                .setFirstName(d.getFirstName())
                .setLastName(d.getLastName())
                .setSpeciality(d.getSpeciality())
                .setLicenseNumber(d.getLicenseNumber())
                .setMedicalUnit(d.getMedicalUnit())
                .setRegisteredAt(d.getUser().getCreatedAt());
    }

    private AdminPharmacyDto mapPharmacy(com.mobylab.springbackend.entity.Pharmacy ph) {
        return new AdminPharmacyDto()
                .setUserId(ph.getUser().getId())
                .setProfileId(ph.getId())
                .setEmail(ph.getUser().getEmail())
                .setStatus(ph.getUser().getStatus())
                .setName(ph.getName())
                .setAddress(ph.getAddress())
                .setCui(ph.getCui())
                .setPharmacyLicense(ph.getPharmacyLicense())
                .setRegisteredAt(ph.getUser().getCreatedAt());
    }

    // ---- Acțiuni ----

    public ResetPasswordResponseDto resetPassword(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Utilizatorul nu a fost găsit!"));

        String tempPassword = generatePassword(10);
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        return new ResetPasswordResponseDto()
                .setTemporaryPassword(tempPassword)
                .setMessage("Parola temporară a fost generată. Comunică-o utilizatorului.");
    }

    public void setUserStatus(UUID userId, UserStatus newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Utilizatorul nu a fost găsit!"));
        user.setStatus(newStatus);
        userRepository.save(user);
    }

    public void approveUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Utilizatorul nu a fost găsit!"));
        if (user.getStatus() != UserStatus.PENDING) {
            throw new BadRequestException("Contul nu este în stare de așteptare.");
        }
        user.setStatus(UserStatus.ACTIVE);
        user.setRejectionReason(null);
        userRepository.save(user);
    }

    public void rejectUser(UUID userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Utilizatorul nu a fost găsit!"));
        if (user.getStatus() != UserStatus.PENDING) {
            throw new BadRequestException("Contul nu este în stare de așteptare.");
        }
        user.setStatus(UserStatus.REJECTED);
        user.setRejectionReason(reason);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Utilizatorul nu a fost găsit!"));
        try {
            userRepository.delete(user);
            userRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(
                "Nu se poate șterge utilizatorul deoarece are date asociate (rețete, etc.). " +
                "Folosiți inactivarea contului sau ștergeți mai întâi datele asociate."
            );
        }
    }

    private String generatePassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}