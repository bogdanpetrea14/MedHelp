package com.mobylab.springbackend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Getter @Setter @Accessors(chain = true)
public class PharmacyStockResponseDto {
    private UUID id;
    private String medicationName;
    private Integer quantity;
    private Double price;
}

