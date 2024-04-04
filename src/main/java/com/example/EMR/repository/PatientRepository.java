package com.example.EMR.repository;

import com.example.EMR.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    @Query("SELECT p FROM Patient p WHERE p.patientId = :patientId")
    Optional<Patient> findPatientById(UUID patientId);
}
