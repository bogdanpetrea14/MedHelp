package com.mobylab.springbackend.entity;

import com.mobylab.springbackend.enums.AllergySeverity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

@Entity
@Table(name = "allergies", schema = "medconnect")
public class Allergy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "active_substance_id", nullable = false)
    private ActiveSubstance activeSubstance;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "severity", nullable = false)
    private AllergySeverity severity;

    @Column(name = "notes")
    private String notes;

    // Getters & Fluent Setters (fără setId)
    public UUID getId() { return id; }
    public Patient getPatient() { return patient; }
    public Allergy setPatient(Patient patient) { this.patient = patient; return this; }
    public ActiveSubstance getActiveSubstance() { return activeSubstance; }
    public Allergy setActiveSubstance(ActiveSubstance activeSubstance) { this.activeSubstance = activeSubstance; return this; }
    public AllergySeverity getSeverity() { return severity; }
    public Allergy setSeverity(AllergySeverity severity) { this.severity = severity; return this; }
    public String getNotes() { return notes; }
    public Allergy setNotes(String notes) { this.notes = notes; return this; }
}