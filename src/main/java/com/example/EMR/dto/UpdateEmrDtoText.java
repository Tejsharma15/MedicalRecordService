package com.example.EMR.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateEmrDtoText {
    private UUID publicEmrId;
    private UUID patientId;
    private String accessDepartments;
    private String comments;
    private String prescription;
    private String tests;
    private String accessList;
}
