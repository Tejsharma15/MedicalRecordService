package com.example.EMR.repository;

import com.example.EMR.models.Emr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmrRepository extends JpaRepository<Emr,UUID> {

    @Query("SELECT e.comments FROM Emr e WHERE e.publicEmrId = :emrId")
    String findCommentHash(UUID emrId);

    @Query("SELECT e.tests FROM Emr e WHERE e.publicEmrId = :emrId")
    String findTestHash(UUID emrId);

    @Query("SELECT e.prescription FROM Emr e WHERE e.publicEmrId = :emrId")
    String findPrescriptionHash(UUID emrId);
    @Modifying
    @Query("UPDATE  Emr e SET e.lastUpdate = :timeVal where e.emrId = :emrId")
    void updateLastUpdate(UUID emrId, Long timeVal);
//    @Modifying
//    @Query("UPDATE  Emr e SET e.accessList = :accessList, e.accessDepartments = :departmentList where e.emrId = :emrId")
//    void updateVals(String accessList, String departmentList, UUID emrId);

    @Modifying
    @Query("DELETE from Emr e where e.patientId = :patientId")
    void deleteByPatientId(UUID patientId);

    @Query("SELECT e.patientId FROM Emr e WHERE e.emrId = :emrId")
    UUID getPatientIdByEmrId(UUID emrId);

    @Query("SELECT e.emrId FROM Emr e WHERE e.patientId = :patientId")
    List<UUID> getEmrByPatientId(UUID patientId);
    @Query("SELECT e.emrId FROM Emr e WHERE e.emrId = :id")
    UUID getEmrIdByPrivateEmrId(UUID id);
    @Modifying
    @Query("UPDATE Emr e SET e.comments = :comments WHERE e.emrId = :emrId")
    void setCommentLocation(UUID emrId, String comments);
    @Modifying
    @Query("UPDATE Emr e SET e.prescription = :prescription WHERE e.emrId = :emrId")
    void setPrescriptionLocation(UUID emrId, String prescription);
    @Modifying
    @Query("UPDATE Emr e SET e.tests = :tests WHERE e.emrId = :emrId")
    void setTestLocation(UUID emrId, String tests);

}
