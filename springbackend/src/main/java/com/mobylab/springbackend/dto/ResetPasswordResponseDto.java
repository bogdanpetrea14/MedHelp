package com.mobylab.springbackend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain = true)
public class ResetPasswordResponseDto {
    private String temporaryPassword;
    private String message;
}