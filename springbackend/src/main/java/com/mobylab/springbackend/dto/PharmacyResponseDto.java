package com.mobylab.springbackend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.UUID;

@Getter @Setter @Accessors(chain = true)
public class PharmacyResponseDto {
    private UUID id;
    private UUID userId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
}