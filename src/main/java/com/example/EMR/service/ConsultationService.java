package com.example.EMR.service;

import com.example.EMR.dto.ConsultationDto;
import com.example.EMR.dto.EmrDto;
import com.example.EMR.models.Consultation;
import com.example.EMR.models.Patient;
import com.example.EMR.models.User;
import com.example.EMR.repository.ConsultationRepository;
import com.example.EMR.repository.PatientRepository;
import com.example.EMR.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final EmrService emrService;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public ConsultationService(ConsultationRepository consultationRepository, EmrService emrService, UserRepository userRepository, PatientRepository patientRepository){
        this.consultationRepository=consultationRepository;
        this.emrService = emrService;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
    }

        public ResponseEntity<?> addConsultation(ConsultationDto consultationdto){
    
            User doctor = userRepository.findByEmployeeId(consultationdto.getDoctorId()).orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
            Patient patient = patientRepository.findPatientById(consultationdto.getPatientId()).orElseThrow(() -> new IllegalArgumentException("Patient not found"));
    
            Consultation consultation = new Consultation();
            consultation.setDoctor(doctor);
            consultation.setPatient(patient);
    
            EmrDto emrDto = new EmrDto();
            emrDto.setPatientId(consultationdto.getPatientId());
            emrDto.setAccessDepartments("");
            emrDto.setAccessList(consultationdto.getDoctorId().toString());
            emrDto.setComments(null);
            emrDto.setPrescription(null);
            System.out.println("Created emrDto to create EMR via consultation");
            UUID publicEmrId = emrService.insertConsulationEmr(emrDto);
    
            consultation.setEmrId(publicEmrId);
            System.out.println("doc: "+consultation.getDoctor().getEmployeeId());
            System.out.println("pat: "+consultation.getPatient().getPatientId());
            System.out.println("id: "+consultation.getEmrId());
            consultationRepository.save(consultation);
            return new ResponseEntity<>("Consultation Added successfully",HttpStatus.OK);
        }

    public List<Consultation> getAllConsultations(){
        return consultationRepository.findAll();
    }
}