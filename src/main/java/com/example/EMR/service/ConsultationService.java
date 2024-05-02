package com.example.EMR.service;

import com.example.EMR.Exception.ResourceNotFoundException;
import com.example.EMR.dto.*;
import com.example.EMR.models.Consultation;
import com.example.EMR.models.Employee_Department;
import com.example.EMR.models.Patient;
import com.example.EMR.models.User;
import com.example.EMR.models.User.EmployeeType;
import com.example.EMR.repository.ConsultationRepository;
import com.example.EMR.repository.EmployeeDepartmentRepository;
import com.example.EMR.repository.PatientRepository;
import com.example.EMR.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final EmrService emrService;
    private final Patient_DepartmentService patientDepartmentService;
    private final Patient_DoctorService patientDoctorService;
    private final EmployeeDepartmentRepository employeeDepartmentRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PublicPrivateService publicPrivateService;

    @Autowired
    public ConsultationService(ConsultationRepository consultationRepository, EmrService emrService,
                               UserRepository userRepository, PatientRepository patientRepository,
                               Patient_DoctorService patientDoctorService, Patient_DepartmentService patientDepartmentService,
                               EmployeeDepartmentRepository employeeDepartmentRepository,
                               PublicPrivateService publicPrivateService) {
        this.consultationRepository = consultationRepository;
        this.emrService = emrService;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.patientDepartmentService = patientDepartmentService;
        this.patientDoctorService = patientDoctorService;
        this.employeeDepartmentRepository = employeeDepartmentRepository;
        this.publicPrivateService = publicPrivateService;
    }

    public ConsultationRequestDto convertConsultationToRequestDto(Consultation obj) {
        ConsultationRequestDto consultationRequestDto = new ConsultationRequestDto();
        consultationRequestDto.setPublicEmrId(obj.getEmrId());
        consultationRequestDto.setPatientId(publicPrivateService.publicIdByPrivateId(obj.getPatient().getPatientId()));
        consultationRequestDto.setDoctorId(publicPrivateService.publicIdByPrivateId(obj.getDoctor().getEmployeeId()));
        return consultationRequestDto;
    }

    public ResponseEntity<?> addConsultation(ConsultationDto consultationdto) throws ResourceNotFoundException, NoSuchAlgorithmException {
        UUID patientPvtId = publicPrivateService.privateIdByPublicId(consultationdto.getPatientId());
        UUID doctorPvtId = publicPrivateService.privateIdByPublicId(consultationdto.getDoctorId());
        User doctor = userRepository.findById(doctorPvtId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        Patient patient = patientRepository.findById(patientPvtId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Consultation consultation = new Consultation();
        consultation.setDoctor(doctor);
        consultation.setIsActive(true);
        consultation.setPatient(patient);

        CreateEmrDtoText createEmrDtoText = new CreateEmrDtoText();
        createEmrDtoText.setPatientId(patientPvtId);
        createEmrDtoText.setAccessDepartments("");
        createEmrDtoText.setAccessList(doctorPvtId.toString());
        createEmrDtoText.setComments("");
        createEmrDtoText.setPrescription("");
        createEmrDtoText.setTests("");
        System.out.println("Created emrDto to create EMR via consultation");
        String publicEmrId = emrService.insertConsulationEmr(createEmrDtoText);

        UpdateEmrDtoText updateEmrDtoText = new UpdateEmrDtoText();
        updateEmrDtoText.setPatientId(consultationdto.getPatientId());
        updateEmrDtoText.setAccessDepartments("");
        updateEmrDtoText.setAccessList(doctorPvtId.toString());
        updateEmrDtoText.setComments("Created EMR");
        updateEmrDtoText.setPrescription("Created EMR");
        updateEmrDtoText.setTests("Created EMR");
        updateEmrDtoText.setPublicEmrId(publicEmrId);
        emrService.updateEmrByIdText(updateEmrDtoText);

        consultation.setEmrId(publicEmrId);
        System.out.println("doc: " + consultation.getDoctor().getEmployeeId());
        System.out.println("pat: " + consultation.getPatient().getPatientId());
        System.out.println("id: " + consultation.getEmrId());
        UUID c_id = consultationRepository.save(consultation).getConsultationId();
        String publicConsultationId = publicPrivateService.savePublicPrivateId(c_id, "CONSULTATION");
        patientDoctorService.addPatient_Doctor(patientPvtId, doctorPvtId);
        List<Employee_Department> u = employeeDepartmentRepository.findDepartmentsByEmployee(doctor);
        for (int i = 0; i < u.size(); i++) {
            patientDepartmentService.addPatient_Department(patientPvtId,
                    u.get(i).getId().getDepartmentId());
        }
        System.out.println("Added to dependency tables");
        ConsultationCreationDto consultationCreationDto = new ConsultationCreationDto();
        consultationCreationDto.setPublicEmrId(consultation.getEmrId());
        consultationCreationDto.setConsultationId(publicConsultationId);
        return  ResponseEntity.ok(consultationCreationDto);
    }

    public ResponseEntity<List<ConsultationRequestDto>> getAllConsultations() {
        List<Consultation> objs = consultationRepository.findAll();
        List<ConsultationRequestDto> response = new ArrayList<ConsultationRequestDto>();

        return ResponseEntity.ok(objs.stream()
                .map(obj -> {
                    ConsultationRequestDto dto = convertConsultationToRequestDto(obj);
                    return dto;
                }).collect(Collectors.toList()));
    }

    public ResponseEntity<ConsultationRequestDto> getConsultationById(String consultationId)
            throws ResourceNotFoundException {
        UUID c_id = publicPrivateService.privateIdByPublicId(consultationId);
        Optional<Consultation> obj = consultationRepository.findById(c_id);
        if (obj.isEmpty()) {
            throw new ResourceNotFoundException("No consultation found for the id: " + consultationId);
        }
        ConsultationRequestDto consultationRequestDto = convertConsultationToRequestDto(obj.get());
        return ResponseEntity.ok(consultationRequestDto);
    }

    public String getEmrIdByPatientIdAndDoctorId(UUID patientId, UUID doctorId) {
        // Check if the patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new IllegalArgumentException("Patient with id " + patientId + " not found");
        }

        // Check if the doctor exists
        User user = userRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor with id " + doctorId + " not found"));

        if (user.getEmployeeType() !=EmployeeType.DOCTOR) {
            throw new IllegalArgumentException("User with id " + doctorId + " is not a doctor");
        }

        Optional<Consultation> consultation = consultationRepository.findByPatientIdAndDoctorIdAndIsActive(patientId,
                doctorId, true);

        if (consultation.isPresent()) {
            return consultation.get().getEmrId();
        } else {
            throw new ResourceNotFoundException("No active consultation found for the given patientId and doctorId");
        }
    }

    public UUID getPatientIdByConsultationId(UUID consultationId) {
        return consultationRepository.getPatientIdByConsultationId(consultationId);
    }
}