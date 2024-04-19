package com.example.EMR.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Data
public class ConsultationDto implements Serializable {
    private String patientId;
    private String doctorId;
}
