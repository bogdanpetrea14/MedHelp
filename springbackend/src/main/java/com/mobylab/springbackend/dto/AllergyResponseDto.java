package com.mobylab.springbackend.dto;

import com.mobylab.springbackend.enums.AllergySeverity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.UUID;

@Getter @Setter @Accessors(chain = true)
public class AllergyResponseDto {
    private UUID id;
    private String activeSubstanceName;
    private AllergySeverity severity;
    private String notes;
    // name
    private String patientName;
}