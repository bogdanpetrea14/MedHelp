package com.mobylab.springbackend.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "prescription_items", schema = "medconnect")
public class PrescriptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne
    @JoinColumn(name = "active_substance_id", nullable = false)
    private ActiveSubstance activeSubstance;

    @Column(name = "dose", nullable = false)
    private String dose;

    @Column(name = "frequency", nullable = false)
    private String frequency;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "notes")
    private String notes;

    // Getters & Setters
    public UUID getId() { return id; }
    public Prescription getPrescription() { return prescription; }
    public PrescriptionItem setPrescription(Prescription prescription) { this.prescription = prescription; return this; }
    public ActiveSubstance getActiveSubstance() { return activeSubstance; }
    public PrescriptionItem setActiveSubstance(ActiveSubstance activeSubstance) { this.activeSubstance = activeSubstance; return this; }
    public String getDose() { return dose; }
    public PrescriptionItem setDose(String dose) { this.dose = dose; return this; }
    public String getFrequency() { return frequency; }
    public PrescriptionItem setFrequency(String frequency) { this.frequency = frequency; return this; }
    public Integer getDurationDays() { return durationDays; }
    public PrescriptionItem setDurationDays(Integer durationDays) { this.durationDays = durationDays; return this; }
    public String getNotes() { return notes; }
    public PrescriptionItem setNotes(String notes) { this.notes = notes; return this; }
}