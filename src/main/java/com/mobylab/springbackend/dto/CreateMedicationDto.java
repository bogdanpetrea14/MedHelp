package com.mobylab.springbackend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Getter @Setter @Accessors(chain = true)
public class CreateMedicationDto {
    @NotNull(message = "ID-ul substanței este obligatoriu!")
    private UUID activeSubstanceId;
    @NotBlank(message = "Brandul este obligatoriu!")
    private String brandName;
    @NotBlank(message = "Concentrația este obligatorie!")
    private String concentration;
    @NotBlank(message = "Forma este obligatorie!")
    private String form;
}