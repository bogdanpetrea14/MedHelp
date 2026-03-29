package com.mobylab.springbackend.dto;

import com.mobylab.springbackend.enums.FeedbackCategory;
import com.mobylab.springbackend.enums.FeedbackRating;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Getter @Setter @Accessors(chain = true)
public class FeedbackResponseDto {
    private UUID id;
    private String userEmail;
    private FeedbackCategory category;
    private FeedbackRating rating;
    private String details;
}

