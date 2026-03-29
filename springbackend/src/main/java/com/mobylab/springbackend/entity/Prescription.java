package com.mobylab.springbackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mobylab.springbackend.enums.PrescriptionStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "prescriptions", schema = "medconnect")
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PrescriptionItem> items = new ArrayList<>();

    public List<PrescriptionItem> getItems() { return items; }
    public Prescription setItems(List<PrescriptionItem> items) { this.items = items; return this; }

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "unique_code", nullable = false, unique = true)
    private String uniqueCode;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private PrescriptionStatus status = PrescriptionStatus.PRESCRIBED;

    @CreationTimestamp
    @Column(name = "prescribed_at", nullable = false, updatable = false)
    private LocalDateTime prescribedAt;

    @Column(name = "picked_up_at")
    private LocalDateTime pickedUpAt;

    @Column(name = "doctor_notes")
    private String doctorNotes;

    // Getters & Fluent Setters
    public UUID getId() { return id; }
    public Doctor getDoctor() { return doctor; }
    public Prescription setDoctor(Doctor doctor) { this.doctor = doctor; return this; }
    public Patient getPatient() { return patient; }
    public Prescription setPatient(Patient patient) { this.patient = patient; return this; }
    public String getUniqueCode() { return uniqueCode; }
    public Prescription setUniqueCode(String uniqueCode) { this.uniqueCode = uniqueCode; return this; }
    public PrescriptionStatus getStatus() { return status; }
    public Prescription setStatus(PrescriptionStatus status) { this.status = status; return this; }
    public LocalDateTime getPrescribedAt() { return prescribedAt; }
    public Prescription setPrescribedAt(LocalDateTime prescribedAt) { this.prescribedAt = prescribedAt; return this; }
    public LocalDateTime getPickedUpAt() { return pickedUpAt; }
    public Prescription setPickedUpAt(LocalDateTime pickedUpAt) { this.pickedUpAt = pickedUpAt; return this; }
    public String getDoctorNotes() { return doctorNotes; }
    public Prescription setDoctorNotes(String doctorNotes) { this.doctorNotes = doctorNotes; return this; }
}