package com.example.EMR.models;

import com.example.EMR.models.CompositePrimaryKeys.Patient_DepartmentId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
@Table(name="patient_department")
public class Patient_Department {
    @EmbeddedId
    private Patient_DepartmentId id;

    @ManyToOne
    @JoinColumn(name = "patientId", referencedColumnName = "patientId", insertable = false, updatable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "departmentId", referencedColumnName = "departmentId", insertable = false, updatable = false)
    private Department department;
}