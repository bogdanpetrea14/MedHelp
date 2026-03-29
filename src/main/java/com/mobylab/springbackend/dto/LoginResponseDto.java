package com.mobylab.springbackend.dto;

import com.mobylab.springbackend.enums.UserRole;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true) // <--- Fără asta, nu poți pune punct după .setToken()
public class LoginResponseDto {
    private String token;
    private long expiresIn;
    private UserRole role;
}