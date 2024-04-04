package com.example.EMR.repository;

import com.example.EMR.models.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ConsultationRepository extends JpaRepository<Consultation,UUID> {

}