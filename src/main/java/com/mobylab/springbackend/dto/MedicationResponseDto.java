package com.mobylab.springbackend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.UUID;

@Getter @Setter @Accessors(chain = true)
public class MedicationResponseDto {
    private UUID id;
    private String brandName;
    private String concentration;
    private String form;
    private UUID activeSubstanceId;
    private String activeSubstanceName;
}