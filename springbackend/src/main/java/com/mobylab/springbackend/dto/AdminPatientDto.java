package com.mobylab.springbackend.dto;

import com.mobylab.springbackend.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Accessors(chain = true)
public class AdminPatientDto {
    private UUID userId;
    private UUID profileId;
    private String email;
    private UserStatus status;
    private String firstName;
    private String lastName;
    private String cnp;
    private LocalDate birthDate;
    private long prescriptionCount;
}