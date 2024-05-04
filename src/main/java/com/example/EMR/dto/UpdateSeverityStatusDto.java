package com.example.EMR.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class UpdateSeverityStatusDto implements Serializable {
    private String consultationId;
    private String status;
}
