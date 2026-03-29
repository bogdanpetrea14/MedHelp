package com.mobylab.springbackend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.UUID;

@Getter @Setter @Accessors(chain = true)
public class DoctorResponseDto {
    private UUID id;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String speciality;
    private String licenseNumber;
}