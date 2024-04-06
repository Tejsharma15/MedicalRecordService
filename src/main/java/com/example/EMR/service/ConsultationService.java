package com.example.EMR.service;

import com.example.EMR.Exception.ResourceNotFoundException;
import com.example.EMR.dto.ConsultationDto;
import com.example.EMR.dto.ConsultationRequestDto;
import com.example.EMR.dto.EmrDto;
import com.example.EMR.models.Consultation;
import com.example.EMR.models.Employee_Department;
import com.example.EMR.models.Patient;
import com.example.EMR.models.User;
import com.example.EMR.repository.ConsultationRepository;
import com.example.EMR.repository.EmployeeDepartmentRepository;
import com.example.EMR.repository.PatientRepository;
import com.example.EMR.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    @Autowired
    public ConsultationService(ConsultationRepository consultationRepository, EmrService emrService, UserRepository userRepository, PatientRepository patientRepository, Patient_DoctorService patientDoctorService, Patient_DepartmentService patientDepartmentService, EmployeeDepartmentRepository employeeDepartmentRepository){
        this.consultationRepository=consultationRepository;
        this.emrService = emrService;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.patientDepartmentService = patientDepartmentService;
        this.patientDoctorService = patientDoctorService;
        this.employeeDepartmentRepository = employeeDepartmentRepository;
    }

    public ConsultationRequestDto convertConsultationToRequestDto(Consultation obj){
        ConsultationRequestDto consultationRequestDto = new ConsultationRequestDto();
        consultationRequestDto.setPublicEmrId(obj.getEmrId());
        consultationRequestDto.setPatientId(obj.getPatient().getPatientId());
        consultationRequestDto.setDoctorId(obj.getDoctor().getEmployeeId());
        return consultationRequestDto;
    }
    public ResponseEntity<?> addConsultation(ConsultationDto consultationdto) throws ResourceNotFoundException{

        User doctor = userRepository.findById(consultationdto.getDoctorId()).orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        Patient patient = patientRepository.findById(consultationdto.getPatientId()).orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

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
        patientDoctorService.addPatient_Doctor(consultationdto.getPatientId(), consultationdto.getDoctorId());
        List<Employee_Department> u = employeeDepartmentRepository.findDepartmentsByEmployee(doctor);
        for(int i=0; i<u.size(); i++){
            patientDepartmentService.addPatient_Department(consultationdto.getPatientId(), u.get(i).getId().getDepartmentId());
        }
        System.out.println("Added to dependency tables");
        return new ResponseEntity<>("Consultation Added successfully",HttpStatus.OK);
    }

    public ResponseEntity<List<ConsultationRequestDto>> getAllConsultations(){
        List<Consultation> objs = consultationRepository.findAll();
        List<ConsultationRequestDto> response = new ArrayList<ConsultationRequestDto>();

        return ResponseEntity.ok(objs.stream()
                .map(obj -> {
                    ConsultationRequestDto dto = convertConsultationToRequestDto(obj);
                    return dto;
                }).collect(Collectors.toList()));
    }

    public ResponseEntity<ConsultationRequestDto> getConsultationById(UUID consultationId) throws ResourceNotFoundException{
        Optional<Consultation> obj = consultationRepository.findById(consultationId);
        if(obj.isEmpty()){
            throw new ResourceNotFoundException("No consultation found for the id: "+ consultationId.toString());
        }
        ConsultationRequestDto consultationRequestDto = convertConsultationToRequestDto(obj.get());
        return ResponseEntity.ok(consultationRequestDto);
    }
}