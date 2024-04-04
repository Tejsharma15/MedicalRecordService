package com.example.EMR.models.CompositePrimaryKeys;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Patient_DepartmentId implements Serializable {
    private UUID patientId;
    private UUID departmentId;
}