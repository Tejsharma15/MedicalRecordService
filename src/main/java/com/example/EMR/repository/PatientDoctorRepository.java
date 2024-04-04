package com.example.EMR.repository;

import com.example.EMR.models.CompositePrimaryKeys.Patient_DoctorId;
import com.example.EMR.models.Patient_Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientDoctorRepository extends JpaRepository<Patient_Doctor, Patient_DoctorId> {

}
