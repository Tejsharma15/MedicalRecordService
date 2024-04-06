package com.example.EMR.models.CompositePrimaryKeys;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class HeadNurse_DepartmentId implements Serializable {
    private UUID headNurseId;
    private UUID departmentId;
}