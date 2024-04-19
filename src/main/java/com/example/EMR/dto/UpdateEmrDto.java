package com.example.EMR.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
public class UpdateEmrDto implements Serializable {

    private String publicEmrId;

    private UUID patientId;
    private String accessDepartments;
    private MultipartFile comments;
    private MultipartFile prescription;
    private MultipartFile tests;
    private String accessList;
}
