package com.example.EMR.service;

import com.example.EMR.Exception.ResourceNotFoundException;
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
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not exist with id " + patientId));

        Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));

        Patient_DepartmentId id = new Patient_DepartmentId(patientId, departmentId);

        if (patientDepartmentRepository.existsById(id)) {
//            throw new ResourceNotFoundException(
//                    "The relationship between the patient and the department already exists");
            return;
        }

        Patient_Department patientDepartment = new Patient_Department();
        patientDepartment.setId(id);
        patientDepartment.setPatient(patient);
        patientDepartment.setDepartment(department);
        patientDepartmentRepository.save(patientDepartment);
    }
}
