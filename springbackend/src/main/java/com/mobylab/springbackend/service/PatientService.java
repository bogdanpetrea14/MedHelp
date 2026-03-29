package com.mobylab.springbackend.service;

import com.mobylab.springbackend.entity.Patient;
import com.mobylab.springbackend.entity.User;
import com.mobylab.springbackend.enums.UserRole;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.PatientRepository;
import com.mobylab.springbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    /**
     * Crearea profilului de pacient (imediat după înregistrarea User-ului)
     */
    @Transactional
    public void createProfile(Patient patientData) {
        // 1. Extragem identitatea din Token-ul JWT
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findUserByEmail(currentUserEmail)
                .orElseThrow(() -> new BadRequestException("Utilizatorul nu a fost găsit!"));

        // 2. BARIERA DE ROL: Doar cine are rolul PATIENT poate avea profil în această tabelă
        if (user.getRole() != UserRole.PATIENT) {
            throw new BadRequestException("Acest cont are rolul " + user.getRole() + " și nu poate crea un profil de pacient!");
        }

        // 3. BARIERA DE UNICITATE (1:1): Un User are un singur profil de pacient
        if (patientRepository.findByUserEmail(currentUserEmail).isPresent()) {
            throw new BadRequestException("Profilul de pacient pentru acest cont există deja!");
        }

        // 4. VALIDARE CNP: Folosim metoda ta din repository
        if (patientRepository.existsByCnp(patientData.getCnp())) {
            throw new BadRequestException("Acest CNP este deja înregistrat în baza noastră de date!");
        }

        // 5. Legăm profilul de user-ul autentificat și salvăm
        patientData.setUser(user);
        patientRepository.save(patientData);
    }

    /**
     * Logica de "Ownership": Doar posesorul profilului sau un ADMIN pot vedea datele
     */
    public Patient getPatientById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Pacientul nu există!"));

        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));

        // Dacă nu e admin și nu e proprietarul profilului -> 403 (aruncăm excepție)
        if (!isAdmin && !patient.getUser().getEmail().equals(currentUserEmail)) {
            throw new BadRequestException("Acces interzis! Nu poți vedea datele altui pacient.");
        }

        return patient;
    }

    /**
     * Metodă utilă pentru Front-end: Îmi dă direct profilul meu fără să-mi știu UUID-ul
     */
    public Patient getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new BadRequestException("Nu ai un profil de pacient configurat!"));
    }
}