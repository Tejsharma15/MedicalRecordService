package com.example.EMR.service;

import com.example.EMR.Exception.ResourceNotFoundException;
import com.example.EMR.models.CompositePrimaryKeys.Patient_DoctorId;
import com.example.EMR.models.Patient;
import com.example.EMR.models.Patient_Doctor;
import com.example.EMR.models.User;
import com.example.EMR.repository.PatientDoctorRepository;
import com.example.EMR.repository.PatientRepository;
import com.example.EMR.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class Patient_DoctorService {
    private final UserRepository employeeRepository;
    private final PatientRepository patientRepository;
    private final PatientDoctorRepository patientDoctorRepository;

    @Autowired
    public Patient_DoctorService(UserRepository userRepository, PatientRepository patientRepository, PatientDoctorRepository patientDoctorRepository){
        this.employeeRepository = userRepository;
        this.patientRepository = patientRepository;
        this.patientDoctorRepository = patientDoctorRepository;
    }
    public void addPatient_Doctor(UUID patientId, UUID doctorId) {
        // Check if the patient and doctor exist
        User doctor = employeeRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not exist with id " + doctorId));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not exist with id " + patientId));

        // Check if the relationship already exists
        Patient_DoctorId id = new Patient_DoctorId(patientId, doctorId);
        if (patientDoctorRepository.existsById(id)) {
//            throw new IllegalArgumentException("The relationship between the patient and the doctor already exists");
            return;
        }

        // Create a new relationship and save it
        Patient_Doctor patientDoctor = new Patient_Doctor();
        patientDoctor.setId(id);
        patientDoctor.setPatient(patient);
        patientDoctor.setDoctor(doctor);
        patientDoctorRepository.save(patientDoctor);
    }
}
