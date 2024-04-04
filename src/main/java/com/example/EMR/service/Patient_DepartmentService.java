package com.example.EMR.service;

import com.example.EMR.models.CompositePrimaryKeys.Patient_DepartmentId;
import com.example.EMR.models.Department;
import com.example.EMR.models.Patient;
import com.example.EMR.models.Patient_Department;
import com.example.EMR.repository.DepartmentRepository;
import com.example.EMR.repository.PatientDepartmentRepository;
import com.example.EMR.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class Patient_DepartmentService {
    private final PatientRepository patientRepository;
    private final DepartmentRepository departmentRepository;
    private final PatientDepartmentRepository patientDepartmentRepository;

    @Autowired
    public Patient_DepartmentService(PatientRepository patientRepository, DepartmentRepository departmentRepository, PatientDepartmentRepository patientDepartmentRepository){
        this.patientRepository = patientRepository;
        this.departmentRepository = departmentRepository;
        this.patientDepartmentRepository = patientDepartmentRepository;
    }
    public void addPatient_Department(UUID patientId, UUID departmentId) {
        Patient patient = patientRepository.findPatientById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not exist with id " + patientId));

        Department department = departmentRepository.findDepartmentById(departmentId).orElseThrow(() -> new IllegalArgumentException("Department not found"));

        Patient_DepartmentId id = new Patient_DepartmentId(patientId, departmentId);

        if (patientDepartmentRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "The relationship between the patient and the department already exists");
        }

        Patient_Department patientDepartment = new Patient_Department();
        patientDepartment.setId(id);
        patientDepartment.setPatient(patient);
        patientDepartment.setDepartment(department);
        patientDepartmentRepository.save(patientDepartment);
    }
}
