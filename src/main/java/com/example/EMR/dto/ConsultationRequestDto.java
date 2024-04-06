package com.example.EMR.dto;

import java.io.Serializable;

public class ConsultationRequestDto implements Serializable {
    private UUID publicEmrId;
    private UUID doctorId;
    private UUID patientId;
}
