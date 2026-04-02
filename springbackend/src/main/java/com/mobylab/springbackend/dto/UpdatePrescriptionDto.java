package com.mobylab.springbackend.dto;

import com.mobylab.springbackend.enums.PrescriptionStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain = true)
public class UpdatePrescriptionDto {
    private String doctorNotes;
    private PrescriptionStatus status;
}