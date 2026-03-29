package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.entity.Doctor;
import com.mobylab.springbackend.entity.Patient;
import com.mobylab.springbackend.service.DoctorService;
import com.mobylab.springbackend.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @PostMapping("/complete-profile")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> completeProfile(@RequestBody Doctor doctor) {
        doctorService.createProfile(doctor);
        return ResponseEntity.ok("Profil de medic creat!");
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Doctor> getMyProfile() {
        return ResponseEntity.ok(doctorService.getMyProfile());
    }
}
