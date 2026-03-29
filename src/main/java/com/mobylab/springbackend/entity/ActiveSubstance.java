package com.mobylab.springbackend.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "active_substances", schema = "medconnect")
public class ActiveSubstance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "category", nullable = false)
    private String category;

    // --- Getters & Setters ---

    public UUID getId() { return id; }

    public String getName() { return name; }
    public ActiveSubstance setName(String name) { this.name = name; return this; }

    public String getDescription() { return description; }
    public ActiveSubstance setDescription(String description) { this.description = description; return this; }

    public String getCategory() { return category; }
    public ActiveSubstance setCategory(String category) { this.category = category; return this; }
}