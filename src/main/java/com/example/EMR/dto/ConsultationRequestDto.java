package com.example.EMR.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class ConsultationRequestDto implements Serializable {
    private UUID publicEmrId;
    private UUID doctorId;
    private UUID patientId;
}
