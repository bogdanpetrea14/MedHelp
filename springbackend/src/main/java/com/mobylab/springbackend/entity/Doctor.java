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
@Table(name = "doctors", schema = "medconnect")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "speciality", nullable = false)
    private String speciality;

    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    // --- Getters & Setters ---

    public UUID getId() { return id; }

    public User getUser() { return user; }
    public Doctor setUser(User user) { this.user = user; return this; }

    public String getFirstName() { return firstName; }
    public Doctor setFirstName(String firstName) { this.firstName = firstName; return this; }

    public String getLastName() { return lastName; }
    public Doctor setLastName(String lastName) { this.lastName = lastName; return this; }

    public String getSpeciality() { return speciality; }
    public Doctor setSpeciality(String speciality) { this.speciality = speciality; return this; }

    public String getLicenseNumber() { return licenseNumber; }
    public Doctor setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; return this; }
}