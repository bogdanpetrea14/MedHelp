package com.mobylab.springbackend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.UUID;

@Data
@Getter @Setter @Accessors(chain = true)
public class PrescriptionItemDto {
    private UUID activeSubstanceId;
    private String activeSubstanceName;
    private String dose;
    private String frequency;
    private Integer durationDays;
    private String notes;
}