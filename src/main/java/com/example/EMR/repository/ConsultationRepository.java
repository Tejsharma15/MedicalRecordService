package com.example.EMR.repository;

import com.example.EMR.models.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ConsultationRepository extends JpaRepository<Consultation,UUID> {

    @Query ("SELECT c FROM Consultation c where c.patient.patientId = :patientId AND c.doctor.employeeId = :doctorId AND c.isActive = :isActive")
    Optional<Consultation> findByPatientIdAndDoctorIdAndIsActive(UUID patientId, UUID doctorId, boolean isActive);

}