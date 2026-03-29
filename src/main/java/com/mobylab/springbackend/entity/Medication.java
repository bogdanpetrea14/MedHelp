package com.mobylab.springbackend.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "medications", schema = "medconnect")
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "active_substance_id", nullable = false)
    private ActiveSubstance activeSubstance;

    @Column(name = "brand_name", nullable = false)
    private String brandName;

    @Column(name = "concentration", nullable = false)
    private String concentration;

    @Column(name = "form", nullable = false)
    private String form;

    // --- Getters & Setters ---

    public UUID getId() { return id; }

    public ActiveSubstance getActiveSubstance() { return activeSubstance; }
    public Medication setActiveSubstance(ActiveSubstance activeSubstance) { this.activeSubstance = activeSubstance; return this; }

    public String getBrandName() { return brandName; }
    public Medication setBrandName(String brandName) { this.brandName = brandName; return this; }

    public String getConcentration() { return concentration; }
    public Medication setConcentration(String concentration) { this.concentration = concentration; return this; }

    public String getForm() { return form; }
    public Medication setForm(String form) { this.form = form; return this; }
}