package com.mobylab.springbackend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

@Getter @Setter @Accessors(chain = true)
public class CreatePrescriptionDto {

    @NotNull(message = "ID-ul pacientului este obligatoriu!")
    private UUID patientId;

    private String doctorNotes;

    @NotEmpty(message = "Reteta trebuie sa contina cel putin un medicament!")
    private List<PrescriptionItemDto> items;
}