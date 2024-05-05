package com.example.EMR.repository;

import com.example.EMR.models.Consultation;
import com.example.EMR.models.Patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultationRepository extends JpaRepository<Consultation,UUID> {

    @Query ("SELECT c FROM Consultation c where c.patient.patientId = :patientId AND c.doctor.employeeId = :doctorId AND c.isActive = :isActive")
    Optional<Consultation> findByPatientIdAndDoctorIdAndIsActive(UUID patientId, UUID doctorId, boolean isActive);
    @Query ("SELECT c.patient.patientId FROM Consultation c where c.consultationId = :consultationId")
    UUID getPatientIdByConsultationId(UUID consultationId);

    @Query ("SELECT c FROM Consultation c where c.patient.patientId = :patientId")
    Optional<Consultation> findByPatientId(UUID patientId);

    @Query ("SELECT c.patient.patientId FROM Consultation c where c.doctor.employeeId = :doctorId")
    List<UUID> getPatientByDoctorId(UUID doctorId);

//    @Transactional
//    @Query("UPDATE Consultation c SET c.doctor = :doctor")
}