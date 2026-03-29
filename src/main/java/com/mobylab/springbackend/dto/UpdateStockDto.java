package com.mobylab.springbackend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class UpdateStockDto {
    @NotNull(message = "ID-ul medicamentului este obligatoriu!")
    private UUID medicationId;
    @NotNull(message = "Cantitatea este obligatorie!")
    private Integer quantity;
    @NotNull(message = "Prețul este obligatoriu!")
    private Double price;
}