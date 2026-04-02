package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.dto.*;
import com.mobylab.springbackend.enums.UserStatus;
import com.mobylab.springbackend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ---- Liste utilizatori ----

    @GetMapping("/users/patients")
    public ResponseEntity<List<AdminPatientDto>> getPatients() {
        return ResponseEntity.ok(adminService.getAllPatients());
    }

    @GetMapping("/users/doctors")
    public ResponseEntity<List<AdminDoctorDto>> getDoctors() {
        return ResponseEntity.ok(adminService.getAllDoctors());
    }

    @GetMapping("/users/pharmacies")
    public ResponseEntity<List<AdminPharmacyDto>> getPharmacies() {
        return ResponseEntity.ok(adminService.getAllPharmacies());
    }

    // ---- Acțiuni ----

    @PostMapping("/users/{userId}/reset-password")
    public ResponseEntity<ResetPasswordResponseDto> resetPassword(@PathVariable UUID userId) {
        return ResponseEntity.ok(adminService.resetPassword(userId));
    }

    @PatchMapping("/users/{userId}/deactivate")
    public ResponseEntity<String> deactivate(@PathVariable UUID userId) {
        adminService.setUserStatus(userId, UserStatus.INACTIVE);
        return ResponseEntity.ok("Contul a fost suspendat.");
    }

    @PatchMapping("/users/{userId}/activate")
    public ResponseEntity<String> activate(@PathVariable UUID userId) {
        adminService.setUserStatus(userId, UserStatus.ACTIVE);
        return ResponseEntity.ok("Contul a fost activat.");
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // ---- Aprobări profesionişti în aşteptare ----

    @GetMapping("/users/pending/doctors")
    public ResponseEntity<List<AdminDoctorDto>> getPendingDoctors() {
        return ResponseEntity.ok(adminService.getPendingDoctors());
    }

    @GetMapping("/users/pending/pharmacies")
    public ResponseEntity<List<AdminPharmacyDto>> getPendingPharmacies() {
        return ResponseEntity.ok(adminService.getPendingPharmacies());
    }

    @PostMapping("/users/{userId}/approve")
    public ResponseEntity<String> approveUser(@PathVariable UUID userId) {
        adminService.approveUser(userId);
        return ResponseEntity.ok("Contul a fost aprobat și activat.");
    }

    @PostMapping("/users/{userId}/reject")
    public ResponseEntity<String> rejectUser(@PathVariable UUID userId, @RequestBody(required = false) RejectDto dto) {
        adminService.rejectUser(userId, dto != null ? dto.getReason() : null);
        return ResponseEntity.ok("Cererea a fost respinsă.");
    }
}