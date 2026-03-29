package com.mobylab.springbackend.entity;

import com.mobylab.springbackend.enums.FeedbackCategory;
import com.mobylab.springbackend.enums.FeedbackRating;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "feedback", schema = "medconnect")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "category", nullable = false)
    private FeedbackCategory category;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "rating", nullable = false)
    private FeedbackRating rating;

    @Column(name = "allow_contact", nullable = false)
    private Boolean allowContact = false;

    @Column(name = "details", nullable = false)
    private String details;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Getters & Setters
    public UUID getId() { return id; }
    public User getUser() { return user; }
    public Feedback setUser(User user) { this.user = user; return this; }
    public FeedbackCategory getCategory() { return category; }
    public Feedback setCategory(FeedbackCategory category) { this.category = category; return this; }
    public FeedbackRating getRating() { return rating; }
    public Feedback setRating(FeedbackRating rating) { this.rating = rating; return this; }
    public Boolean getAllowContact() { return allowContact; }
    public Feedback setAllowContact(Boolean allowContact) { this.allowContact = allowContact; return this; }
    public String getDetails() { return details; }
    public Feedback setDetails(String details) { this.details = details; return this; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Feedback setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
}