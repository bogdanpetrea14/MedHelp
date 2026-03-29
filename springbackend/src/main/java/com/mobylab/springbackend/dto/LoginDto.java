package com.mobylab.springbackend.dto;

import com.mobylab.springbackend.enums.UserRole;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter @Setter @Accessors(chain = true)
public class LoginDto {
    private String email;
    private String password;
}