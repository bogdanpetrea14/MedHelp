package com.mobylab.springbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;

@Getter @Setter @Accessors(chain = true)
public class LoginResponseDto {

    @JsonProperty("access_token")
    private String token;

    @JsonProperty("token_type")
    private String type = "Bearer";

    @JsonProperty("expires_in")
    @Value("${token.ttl}")
    private long expire;
}