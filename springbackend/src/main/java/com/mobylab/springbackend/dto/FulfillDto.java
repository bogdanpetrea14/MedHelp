package com.mobylab.springbackend.dto;

import com.mobylab.springbackend.enums.PrescriptionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class FulfillDto {

    @NotNull(message = "Trebuie să specifici ce medicament dai pacientului!")
    private UUID medicationId;

    @NotNull(message = "Trebuie să specifici cantitatea eliberată!")
    private Integer quantity;

    @NotNull(message = "Trebuie să specifici noul status al rețetei!")
    private PrescriptionStatus status; // Aici vom primi FULFILLED sau PARTIALLY_FULFILLED
}