package com.example.EMR.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.UUID;

@Data
public class EmrDto implements Serializable {
    private UUID patientId;
    private String accessDepartments;
    private MultipartFile comments;
    private MultipartFile prescription;
    private String accessList;
}
