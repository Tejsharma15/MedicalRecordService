package com.example.EMR.service;

import com.example.EMR.models.Patient;
import com.example.EMR.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class PatientService {
    PatientRepository patientRepository;
    @Autowired
    PatientService(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }

    public boolean verifyPatient(UUID privateId){
        Patient patient = patientRepository.findById(privateId).orElse(null);
        if (patient != null) {
            Patient.PatientType patientType = patient.getPatientType();
            return patientType == Patient.PatientType.NOT_VERIFIED;
        }
        return false;
    }
}
