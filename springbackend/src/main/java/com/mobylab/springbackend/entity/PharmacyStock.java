package com.mobylab.springbackend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pharmacy_stock", schema = "medconnect")
public class PharmacyStock {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @ManyToOne
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @Column(name = "price", nullable = false)
    private Double price;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Getters & Setters
    public UUID getId() { return id; }
    public Pharmacy getPharmacy() { return pharmacy; }
    public PharmacyStock setPharmacy(Pharmacy pharmacy) { this.pharmacy = pharmacy; return this; }
    public Medication getMedication() { return medication; }
    public PharmacyStock setMedication(Medication medication) { this.medication = medication; return this; }
    public Integer getQuantity() { return quantity; }
    public PharmacyStock setQuantity(Integer quantity) { this.quantity = quantity; return this; }
    public Double getPrice() { return price; }
    public PharmacyStock setPrice(Double price) { this.price = price; return this; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public PharmacyStock setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
}