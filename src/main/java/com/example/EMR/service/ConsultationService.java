package com.example.EMR.service;

import com.example.EMR.Exception.ResourceNotFoundException;
import com.example.EMR.dto.*;
import com.example.EMR.models.Consultation;
import com.example.EMR.models.Employee_Department;
import com.example.EMR.models.Patient;
import com.example.EMR.models.Patient_Doctor;
import com.example.EMR.models.User;
import com.example.EMR.models.User.EmployeeType;
import com.example.EMR.repository.ConsultationRepository;
import com.example.EMR.repository.EmployeeDepartmentRepository;
import com.example.EMR.repository.PatientDoctorRepository;
import com.example.EMR.repository.PatientRepository;
import com.example.EMR.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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

    public ResponseEntity<?> addConsultation(ConsultationDto consultationdto) throws ResourceNotFoundException, NoSuchAlgorithmException, IOException {
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
        // updateEmrDtoText.setComments("Created EMR");
        // updateEmrDtoText.setPrescription("Created EMR");
        // updateEmrDtoText.setTests("Created EMR");
        updateEmrDtoText.setPublicEmrId(publicEmrId);
        emrService.updateEmrByIdText(updateEmrDtoText);

        consultation.setEmrId(publicEmrId);
        consultation.setSeverity(Consultation.Severity.MEDIUM);
        System.out.println("doc: " + consultation.getDoctor().getEmployeeId());
        System.out.println("pat: " + consultation.getPatient().getPatientId());
        System.out.println("id: " + consultation.getEmrId());
        if(patientDoctorService.checkIfRelationshipExists(patientPvtId, doctorPvtId)){
            UUID consultationId = getConsultationIdByPatientIdAndDoctorId(patientPvtId, doctorPvtId);
            String emrId = getEmrIdByPatientIdAndDoctorId(patientPvtId, doctorPvtId);

            ConsultationCreationDto consultationCreationDto = new ConsultationCreationDto();
            consultationCreationDto.setPublicEmrId(consultation.getEmrId());
            consultationCreationDto.setConsultationId(publicPrivateService.publicIdByPrivateId(consultationId)) ;
            return  ResponseEntity.ok(consultationCreationDto);
        }
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

    public UUID getConsultationIdByPatientIdAndDoctorId(UUID patientId, UUID doctorId) {
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
            return consultation.get().getConsultationId();
        } else {
            throw new ResourceNotFoundException("No active consultation found for the given patientId and doctorId");
        }
    }

    public UUID getPatientIdByConsultationId(UUID consultationId) {
        return consultationRepository.getPatientIdByConsultationId(consultationId);
    }
    
    public ResponseEntity<?> updateConsultation(UpdateConsultationDto updateConsultationDto) {
        //does 2 things. 1) Delete existing patient-doctor pair and 2) Add new pair
        // UUID patientPvtId = publicPrivateService.privateIdByPublicId(updateConsultationDto.getPatientId());
        List<UUID>PatientPvtIds = consultationRepository.getPatientByDoctorId(publicPrivateService.privateIdByPublicId(updateConsultationDto.getDoctorId()));
        System.out.println(PatientPvtIds);
        UUID doctorNewPvtId = publicPrivateService.privateIdByPublicId(updateConsultationDto.getNewDoctorId());
        UUID doctorPvtId = publicPrivateService.privateIdByPublicId(updateConsultationDto.getDoctorId());
        for(int i=0; i<PatientPvtIds.size(); i++){
            
            Optional<Consultation> c = consultationRepository.findByPatientIdAndDoctorIdAndIsActive(PatientPvtIds.get(i), doctorPvtId, true);
            if(c.isEmpty()){
                return new ResponseEntity<>("Successfully updated consultation", HttpStatus.OK);
            }
            // UUID consultationId = c.get().getConsultationId();
            patientDoctorService.addPatient_Doctor(PatientPvtIds.get(i), doctorNewPvtId);
            patientDoctorService.deletePatient_Doctor(PatientPvtIds.get(i), doctorPvtId);
            // Optional<Consultation> consultation = consultationRepository.findById(consultationId);
            // if(c.isEmpty()){
            //     throw new IllegalArgumentException("Consultation with id " + consultationId + " not found");
            // }
            Consultation consultation = c.get();
            User doctor = userRepository.findById(doctorNewPvtId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            consultation.setDoctor(doctor);
            consultationRepository.save(consultation);
        }
        return new ResponseEntity<>("Successfully updated consultation", HttpStatus.OK);
    }
    public ResponseEntity<?> updateSeverity(UpdateSeverityStatusDto updateSeverityStatusDto){

        Optional<Consultation> consultation = consultationRepository.findById(publicPrivateService.privateIdByPublicId(updateSeverityStatusDto.getConsultationId()));
        if(consultation.isEmpty()){
            throw new IllegalArgumentException("Consultation with id " + updateSeverityStatusDto.getConsultationId() + " not found");
        }
        Consultation c = consultation.get();
        Consultation.Severity newSeverity = Consultation.Severity.valueOf(updateSeverityStatusDto.getStatus().toUpperCase());
        c.setSeverity(newSeverity);
        consultationRepository.save(c);
        return new ResponseEntity<>("Updated status of the consultation with id" + updateSeverityStatusDto.getConsultationId() + "with status: "+updateSeverityStatusDto.getStatus(), HttpStatus.OK);

    }

    public ResponseEntity<?> getSeverity(UUID consultationId) {
//        System.out.println(consultationId);

        Optional<Consultation> consultation = consultationRepository.findById(consultationId);
        if(consultation.isEmpty()){
            throw new IllegalArgumentException("Consultation with id " + consultationId + " not found");
        }
        System.out.println("WTH");
        Consultation c = consultation.get();
        if(c.getSeverity() == Consultation.Severity.LOW) return new ResponseEntity<>("LOW", HttpStatus.OK);
        if(c.getSeverity() == Consultation.Severity.HIGH) return new ResponseEntity<>("HIGH", HttpStatus.OK);
        return new ResponseEntity<>("MEDIUM", HttpStatus.OK);

    }
    public Consultation.Severity getSeverityPatient(UUID PatientId) {
//        System.out.println(consultationId);

        Optional<Consultation> consultation = consultationRepository.findByPatientId(PatientId);
        if(consultation.isEmpty()){
            throw new IllegalArgumentException("Consultation with id " + PatientId + " not found");
        }
        return consultation.get().getSeverity();
    }
    public Map<String, String> getAllConsultationsWithSeverity() {
        List<Consultation> consultations = consultationRepository.findAll();

        Map<String, String> consultationsWithSeverity = consultations.stream()
                .collect(Collectors.toMap(
                        consultation -> publicPrivateService.publicIdByPrivateId(consultation.getConsultationId()), // Assuming you have a method to get public ID from private ID
                        consultation -> {
                            switch (consultation.getSeverity()) {
                                case LOW:
                                    return "LOW";
                                case MEDIUM:
                                    return "MEDIUM";
                                case HIGH:
                                    return "HIGH";
                                default:
                                    return "UNKNOWN";
                            }
                        }
                ));

        return consultationsWithSeverity;
    }
}