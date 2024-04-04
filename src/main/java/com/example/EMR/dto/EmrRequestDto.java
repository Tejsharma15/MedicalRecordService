package com.example.EMR.dto;

import java.sql.Blob;
import java.util.UUID;

public class EmrRequestDto {

    private UUID patientId;
    private String accessDepartments;
    private Blob comments;
    private Blob prescription;
    private String accessList;
}
