package com.mobylab.springbackend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter @Setter @Accessors(chain = true)
public class CreatePharmacyDto {
    @NotBlank(message = "Numele farmaciei este obligatoriu!")
    private String name;
    @NotBlank(message = "Adresa este obligatorie!")
    private String address;
    @NotNull(message = "Latitudinea este obligatorie!")
    private Double latitude;
    @NotNull(message = "Longitudinea este obligatorie!")
    private Double longitude;
}