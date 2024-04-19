package com.example.EMR.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ConsultationRequestDto implements Serializable {
    private String publicEmrId;
    private String doctorId;
    private String patientId;
}
