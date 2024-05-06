package com.example.EMR.repository;

import com.example.EMR.models.CompositePrimaryKeys.Patient_DepartmentId;
import com.example.EMR.models.Department;
import com.example.EMR.models.Employee_Department;
import com.example.EMR.models.Patient;
import com.example.EMR.models.Patient_Department;
import com.example.EMR.models.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientDepartmentRepository extends JpaRepository<Patient_Department, Patient_DepartmentId> {                            
    List<Patient_Department> findPatientsByDepartment(Department department);

    List<Patient_Department> findDepartmentsByPatient(Patient patient);

}
