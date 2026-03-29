package com.mobylab.springbackend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.UUID;

@Getter @Setter @Accessors(chain = true)
public class ActiveSubstanceResponseDto {
    private UUID id;
    private String name;
    private String description;
    private String category;
}