package com.mobylab.springbackend.dto;

import com.mobylab.springbackend.enums.AllergySeverity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Getter @Setter @Accessors(chain = true)
public class CreateAllergyDto {
    @NotNull(message = "ID-ul substanței active este obligatoriu!")
    private UUID activeSubstanceId;
    @NotNull(message = "Severitatea este obligatorie!")
    private AllergySeverity severity;
    private String notes;
}