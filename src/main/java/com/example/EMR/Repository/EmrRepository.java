package com.example.EMR.Repository;

import com.example.EMR.Model.Emr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmrRepository extends JpaRepository<Emr,UUID> {

    @Query("SELECT e.comments FROM Emr e WHERE e.publicEmrId = :emrId")
    String findCommentHash(UUID emrId);

    @Query("SELECT e.prescription FROM Emr e WHERE e.publicEmrId = :emrId")
    String findPrescriptionHash(UUID emrId);
    @Modifying
    @Query("UPDATE  Emr e SET e.lastUpdate = :timeVal where e.publicEmrId = :emrId")
    void updateLastUpdate(UUID emrId, Long timeVal);
    @Modifying
    @Query("UPDATE  Emr e SET e.accessList = :accessList, e.accessDepartments = :departmentList where e.publicEmrId = :emrId")
    void updateVals(String accessList, String departmentList, UUID emrId);

    @Modifying
    @Query("DELETE from Emr e where e.patientId = :patientId")
    void deleteByPatientId(UUID patientId);

    @Query("SELECT e.patientId FROM Emr e WHERE e.publicEmrId = :emrId")
    UUID getPatientIdByEmrId(UUID emrId);
}
