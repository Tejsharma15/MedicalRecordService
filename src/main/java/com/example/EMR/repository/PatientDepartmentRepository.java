package com.example.EMR.repository;

import com.example.EMR.models.CompositePrimaryKeys.Patient_DepartmentId;
import com.example.EMR.models.Patient_Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientDepartmentRepository extends JpaRepository<Patient_Department, Patient_DepartmentId> {
}
