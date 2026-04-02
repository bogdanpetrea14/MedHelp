package com.mobylab.springbackend.dto;

import com.mobylab.springbackend.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @Accessors(chain = true)
public class AdminPharmacyDto {
    private UUID userId;
    private UUID profileId;
    private String email;
    private UserStatus status;
    private String name;
    private String address;
    private String cui;
    private String pharmacyLicense;
    private LocalDateTime registeredAt;
}