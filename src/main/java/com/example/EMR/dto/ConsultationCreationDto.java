package com.example.EMR.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;


@Data
public class ConsultationCreationDto implements Serializable {
    private String consultationId;
    private String publicEmrId;
}
