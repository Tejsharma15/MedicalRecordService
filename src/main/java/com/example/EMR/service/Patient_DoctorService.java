package com.example.EMR.service;

import com.example.EMR.Exception.ResourceNotFoundException;
import com.example.EMR.dto.PatientRequestSeverityDto;
import com.example.EMR.models.CompositePrimaryKeys.Patient_DoctorId;
import com.example.EMR.models.Consultation;
import com.example.EMR.models.Consultation.Severity;
import com.example.EMR.models.Patient;
import com.example.EMR.models.Patient.*;
import com.example.EMR.models.Patient_Doctor;
import com.example.EMR.models.User;
import com.example.EMR.repository.ConsultationRepository;
import com.example.EMR.repository.PatientDoctorRepository;
import com.example.EMR.repository.PatientRepository;
import com.example.EMR.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class Patient_DoctorService {
    private final UserRepository employeeRepository;

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final PatientDoctorRepository patientDoctorRepository;
    private final PublicPrivateService publicPrivateService;

    @Autowired
    public Patient_DoctorService(UserRepository userRepository, ConsultationRepository consultationRepository, PatientRepository patientRepository, PatientDoctorRepository patientDoctorRepository, PublicPrivateService publicPrivateService){
        this.employeeRepository = userRepository;
        this.consultationRepository = consultationRepository;
        this.patientRepository = patientRepository;
        this.patientDoctorRepository = patientDoctorRepository;
        this.publicPrivateService = publicPrivateService;
    }
    public void addPatient_Doctor(UUID patientId, UUID doctorId) {
        // Check if the patient and doctor exist
        User doctor = employeeRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not exist with id " + doctorId));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not exist with id " + patientId));

        // Check if the relationship already exists
        Patient_DoctorId id = new Patient_DoctorId(patientId, doctorId);
        if (patientDoctorRepository.existsById(id)) {
//            throw new IllegalArgumentException("The relationship between the patient and the doctor already exists");
            return;
        }

        // Create a new relationship and save it
        Patient_Doctor patientDoctor = new Patient_Doctor();
        patientDoctor.setId(id);
        patientDoctor.setPatient(patient);
        patientDoctor.setDoctor(doctor);
        patientDoctorRepository.save(patientDoctor);
    }

    public boolean checkIfRelationshipExists(UUID patientId, UUID doctorId) {
        return patientDoctorRepository.existsByPatient_PatientIdAndDoctor_EmployeeId(patientId, doctorId);
    }

    public void deletePatient_Doctor(UUID patientId, UUID doctorId){
        User doctor = employeeRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not exist with id " + doctorId));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not exist with id " + patientId));
        patientDoctorRepository.deleteByPatientIdAndDoctorId(patientId, doctorId);
    }

    public PatientRequestSeverityDto convertPatientToDto(Patient patient, Severity sev){
        PatientRequestSeverityDto dto = new PatientRequestSeverityDto();
        dto.setPatientId(publicPrivateService.publicIdByPrivateId(patient.getPatientId()));
        dto.setName(patient.getName());
        dto.setAabhaId(patient.getAabhaId());
        // dto.setAadharId(patient.getAadharId());
        dto.setEmailId(patient.getEmailId());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setEmergencyContactNumber(patient.getEmergencyContactNumber());
        dto.setGender(patient.getGender());
        dto.setPatientType(patient.getPatientType());
        dto.setDischargeStatus(patient.getDischargeStatus());
        dto.setSeverity(sev);
        return dto;
    }

    public ResponseEntity<?> getAllInpatientsByDoctorID(UUID doctorId) {
        // TODO: DOCTOR ID CHECK KRNI HAI
        List<Patient_Doctor> patient_Doctor = patientDoctorRepository.findByDoctorId(doctorId);
        List<Patient> patients = patient_Doctor.stream()
                .map(Patient_Doctor::getPatient)
                .filter(patient -> patient.getPatientType() == PatientType.INPATIENT).toList();
        if (patients.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(
                    patients.stream()
                            .map(patient -> {
                                Consultation.Severity sev = getSeverityPatient(patient.getPatientId());
                                PatientRequestSeverityDto dto = convertPatientToDto(patient, sev);
                                return dto;
                            }).collect(Collectors.toList()));
        }
    }

    public ResponseEntity<?> getAllOutpatientsByDoctorID(UUID doctorId) {
        List<Patient_Doctor> patient_Doctor = patientDoctorRepository.findByDoctorId(doctorId);
        List<Patient> patients = patient_Doctor.stream()
                .map(Patient_Doctor::getPatient).filter(patient -> patient.getPatientType() == PatientType.OUTPATIENT)
                .toList();
        if (patients.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(
                    patients.stream()
                            .map(patient -> {
                                Consultation.Severity sev = getSeverityPatient(patient.getPatientId());
                                PatientRequestSeverityDto dto = convertPatientToDto(patient, sev);
                                return dto;
                            }).collect(Collectors.toList()));
        }
    }
    public Consultation.Severity getSeverityPatient(UUID PatientId) {
//        System.out.println(consultationId);

        Optional<Consultation> consultation = consultationRepository.findByPatientId(PatientId);
        if(consultation.isEmpty()){
            throw new IllegalArgumentException("Consultation with id " + PatientId + " not found");
        }
        return consultation.get().getSeverity();
    }
}
