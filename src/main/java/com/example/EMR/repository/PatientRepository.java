package com.example.EMR.repository;

import com.example.EMR.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
;
}
