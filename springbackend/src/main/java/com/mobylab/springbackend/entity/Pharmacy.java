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
@Table(name = "pharmacies", schema = "medconnect")
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    // --- Getters & Setters ---

    public UUID getId() { return id; }

    public User getUser() { return user; }
    public Pharmacy setUser(User user) { this.user = user; return this; }

    public String getName() { return name; }
    public Pharmacy setName(String name) { this.name = name; return this; }

    public String getAddress() { return address; }
    public Pharmacy setAddress(String address) { this.address = address; return this; }

    public Double getLatitude() { return latitude; }
    public Pharmacy setLatitude(Double latitude) { this.latitude = latitude; return this; }

    public Double getLongitude() { return longitude; }
    public Pharmacy setLongitude(Double longitude) { this.longitude = longitude; return this; }
}