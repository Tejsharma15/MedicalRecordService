package com.example.EMR.repository;

import com.example.EMR.models.CompositePrimaryKeys.Patient_DoctorId;
import com.example.EMR.models.Patient_Doctor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PatientDoctorRepository extends JpaRepository<Patient_Doctor, Patient_DoctorId> {
    @Modifying
    @Transactional
    @Query("DELETE from Patient_Doctor pd where pd.patient.patientId = :patientId and pd.doctor.employeeId = :doctorId")
    void deleteByPatientIdAndDoctorId(UUID patientId, UUID doctorId);

    @Query("SELECT pd FROM Patient_Doctor pd WHERE pd.doctor.employeeId = :doctorId")
    List<Patient_Doctor> findByDoctorId(UUID doctorId);

}
