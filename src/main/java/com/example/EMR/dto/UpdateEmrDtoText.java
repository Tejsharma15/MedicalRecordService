package com.example.EMR.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UpdateEmrDtoText{
    private String publicEmrId;
    private String patientId;
    private String accessDepartments;
    private String[] comments;
    private String[] prescription;
    private String[] tests;
    private String commentst;
    private String prescriptiont;
    private String testst;
    private String accessList;
    private int isImage;
    private int isText;
}
