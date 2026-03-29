package com.mobylab.springbackend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @Accessors(chain = true)
public class PatientResponseDto {
    private UUID id;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String cnp;
    private LocalDate birthDate;
    private UUID primaryDoctorId;
    private String primaryDoctorName;
}