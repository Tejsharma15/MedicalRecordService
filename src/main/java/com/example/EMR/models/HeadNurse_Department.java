package com.example.EMR.models;

import com.example.EMR.models.CompositePrimaryKeys.HeadNurse_DepartmentId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name="headnurse_department")
public class HeadNurse_Department {
    @EmbeddedId
    private HeadNurse_DepartmentId id;

    @OneToOne
    @JoinColumn(name = "headNurseId", referencedColumnName = "employeeId", insertable = false, updatable = false)
    private User headNurse;

    @OneToOne
    @JoinColumn(name = "departmentId", referencedColumnName = "departmentId", insertable = false,updatable = false)
    private Department department;
}
