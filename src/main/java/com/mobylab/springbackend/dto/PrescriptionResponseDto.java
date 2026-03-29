package com.mobylab.springbackend.dto;

import com.mobylab.springbackend.enums.PrescriptionStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @Accessors(chain = true)
public class PrescriptionResponseDto {
    private UUID id;
    private String doctorName;
    private String patientName;
    private String uniqueCode;
    private PrescriptionStatus status;
    private LocalDateTime prescribedAt;
    private String doctorNotes;
}