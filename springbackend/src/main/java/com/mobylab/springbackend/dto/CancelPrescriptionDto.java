package com.mobylab.springbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain = true)
public class CancelPrescriptionDto {
    @NotBlank(message = "Motivul anulării este obligatoriu!")
    private String reason;
}