package com.example.EMR.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Data
public class ConsultationDto implements Serializable {
    private UUID patientId;
    private UUID doctorId;
}
