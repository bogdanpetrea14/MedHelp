package com.mobylab.springbackend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Getter @Setter @Accessors(chain = true)
public class CreatePrescriptionDto {
    @NotNull(message = "ID-ul pacientului este obligatoriu!")
    private UUID patientId;
    @NotBlank(message = "Codul unic al rețetei este obligatoriu!")
    private String uniqueCode;
    private String doctorNotes;
}