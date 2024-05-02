package com.example.EMR.repository;

import com.example.EMR.models.CompositePrimaryKeys.Patient_DoctorId;
import com.example.EMR.models.Patient_Doctor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientDoctorRepository extends JpaRepository<Patient_Doctor, Patient_DoctorId> {
    @Transactional
    void deleteByPatientIdAndDoctorId(UUID patientId, UUID doctorId);
}
