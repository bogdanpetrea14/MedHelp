package com.mobylab.springbackend.entity;

import com.mobylab.springbackend.enums.UserRole;
import com.mobylab.springbackend.enums.UserStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patients", schema = "medconnect")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "cnp", nullable = false, unique = true)
    private String cnp;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "primary_doctor_id")
    private Doctor primaryDoctor;

    public UUID getId() { return id; }

    public User getUser() { return user; }
    public Patient setUser(User user) { this.user = user; return this; }

    public String getFirstName() { return firstName; }
    public Patient setFirstName(String firstName) { this.firstName = firstName; return this; }

    public String getLastName() { return lastName; }
    public Patient setLastName(String lastName) { this.lastName = lastName; return this; }

    public String getCnp() { return cnp; }
    public Patient setCnp(String cnp) { this.cnp = cnp; return this; }

    public LocalDate getBirthDate() { return birthDate; }
    public Patient setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; return this; }

    public Doctor getPrimaryDoctor() { return primaryDoctor; }
    public Patient setPrimaryDoctor(Doctor primaryDoctor) { this.primaryDoctor = primaryDoctor; return this; }
}
