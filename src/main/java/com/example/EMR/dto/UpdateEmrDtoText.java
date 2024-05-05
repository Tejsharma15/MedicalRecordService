package com.example.EMR.dto;

import lombok.Data;

@Data
public class UpdateEmrDtoText {
    private String publicEmrId;
    private String patientId;
    private String accessDepartments;
    private String[] comments;
    private String[] prescription;
    private String[] tests;
    private String accessList;
}
