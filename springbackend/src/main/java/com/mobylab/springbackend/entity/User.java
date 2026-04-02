package com.mobylab.springbackend.entity;

import com.mobylab.springbackend.enums.UserRole;
import com.mobylab.springbackend.enums.UserStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    // --- Getters & Setters ---

    public UUID getId() { return id; }

    public String getEmail() { return email; }
    public User setEmail(String email) { this.email = email; return this; }

    public String getPassword() { return password; }
    public User setPassword(String password) { this.password = password; return this; }

    public UserRole getRole() { return role; }
    public User setRole(UserRole role) { this.role = role; return this; }

    public UserStatus getStatus() { return status; }
    public User setStatus(UserStatus status) { this.status = status; return this; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public User setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

    public String getRejectionReason() { return rejectionReason; }
    public User setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; return this; }
}