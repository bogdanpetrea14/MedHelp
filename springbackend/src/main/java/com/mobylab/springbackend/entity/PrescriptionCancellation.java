package com.mobylab.springbackend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "prescription_cancellations", schema = "medconnect")
public class PrescriptionCancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "prescription_id", nullable = false, unique = true)
    private Prescription prescription;

    @ManyToOne
    @JoinColumn(name = "cancelled_by", nullable = false)
    private User cancelledBy;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @CreationTimestamp
    @Column(name = "cancelled_at", nullable = false, updatable = false)
    private LocalDateTime cancelledAt;

    public UUID getId() { return id; }

    public Prescription getPrescription() { return prescription; }
    public PrescriptionCancellation setPrescription(Prescription prescription) { this.prescription = prescription; return this; }

    public User getCancelledBy() { return cancelledBy; }
    public PrescriptionCancellation setCancelledBy(User cancelledBy) { this.cancelledBy = cancelledBy; return this; }

    public String getReason() { return reason; }
    public PrescriptionCancellation setReason(String reason) { this.reason = reason; return this; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
}