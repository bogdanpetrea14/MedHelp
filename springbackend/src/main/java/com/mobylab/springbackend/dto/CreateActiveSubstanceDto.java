package com.mobylab.springbackend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import jakarta.validation.constraints.NotBlank;

@Getter @Setter @Accessors(chain = true)
public class CreateActiveSubstanceDto {
    @NotBlank(message = "Numele este obligatoriu!")
    private String name;
    private String description;
    @NotBlank(message = "Categoria este obligatorie!")
    private String category;
}