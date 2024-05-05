package com.example.EMR.dto;
import java.io.Serializable;

import com.example.EMR.models.Consultation;
import com.example.EMR.models.Consultation.Severity;
import com.example.EMR.models.Patient.DischargeStatus;
import com.example.EMR.models.Patient.Gender;
import com.example.EMR.models.Patient.PatientType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class PatientRequestSeverityDto implements Serializable {
    private String patientId;
    private String name;
    private String aabhaId;
    // private String aadharId;
    private String emailId;
    private String dateOfBirth;
    private String emergencyContactNumber;
    private Gender gender;
    private PatientType patientType;
    private DischargeStatus dischargeStatus;
    private Severity severity;
}
