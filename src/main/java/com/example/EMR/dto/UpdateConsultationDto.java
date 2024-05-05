package com.example.EMR.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class UpdateConsultationDto implements Serializable {
    private String doctorId;
    private String newDoctorId;
}
