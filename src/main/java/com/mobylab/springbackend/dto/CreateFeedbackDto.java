package com.mobylab.springbackend.dto;

import com.mobylab.springbackend.enums.FeedbackCategory;
import com.mobylab.springbackend.enums.FeedbackRating;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;


@Getter
@Setter
@Accessors(chain = true)
public class CreateFeedbackDto {
    @NotNull(message = "Categoria este obligatorie!")
    private FeedbackCategory category;
    @NotNull(message = "Rating-ul este obligatoriu!")
    private FeedbackRating rating;
    @NotBlank(message = "Detaliile sunt obligatorii!")
    private String details;
    private Boolean allowContact = false;
}