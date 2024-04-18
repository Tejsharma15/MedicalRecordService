package com.example.EMR.controller;

import com.example.EMR.Exception.ResourceNotFoundException;
import com.example.EMR.dto.ConsultationDto;
import com.example.EMR.dto.ConsultationRequestDto;
import com.example.EMR.service.ConsultationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/consultation")
public class ConsultationController {
    private ConsultationService consultationService;
    private static final Logger logger = LogManager.getLogger("com.example");

    @Autowired
    public ConsultationController(ConsultationService consultationService){
        this.consultationService=consultationService;
    }

    @PostMapping("/addConsultation")
    @PreAuthorize("hasAuthority('admin:create') or hasAuthority('desk:create')")
    public ResponseEntity<?>addConsultation(@RequestBody ConsultationDto consultationdto) throws ResourceNotFoundException, NoSuchAlgorithmException {
        System.out.println("Adding consultation");
        String username = UUID.randomUUID().toString();
        ThreadContext.put("actorUUID", username);
        System.out.println(ThreadContext.get("actorUUID"));
        logger.info("POST: Adding consultation");
        ThreadContext.clearAll();
        return consultationService.addConsultation(consultationdto);
    }

    @GetMapping("/getAllConsultations")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<List<ConsultationRequestDto>> getAllConsultations(){
        String username = UUID.randomUUID().toString();
        ThreadContext.put("actorUUID", username);
        System.out.println(ThreadContext.get("actorUUID"));
        logger.info("GET: All consultations");
        ThreadContext.clearAll();
        return consultationService.getAllConsultations();
    }

    @GetMapping("/getConsultationById/{consultationId}")
    @PreAuthorize(("hasAuthority('admin:read')"))
    public ResponseEntity<ConsultationRequestDto> getConsultationById(@PathVariable ("consultationId") UUID consultationId){
        String username = UUID.randomUUID().toString();
        ThreadContext.put("actorUUID", username);
        System.out.println(ThreadContext.get("actorUUID"));
        logger.info("GET: Consultation ID");
        ThreadContext.clearAll();
        return consultationService.getConsultationById(consultationId);
    }

    @GetMapping("/getEmrIdByPatientIdAndDoctorId")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<?> getEmrIdByPatientIdAndDoctorId(@RequestParam UUID patientId, @RequestParam UUID doctorId){
        String username = UUID.randomUUID().toString();
        ThreadContext.put("actorUUID", username);
        System.out.println(ThreadContext.get("actorUUID"));
        logger.info("GET: EMR-ID from patient and doctor id");
        ThreadContext.clearAll();
        try {
            return ResponseEntity.ok(consultationService.getEmrIdByPatientIdAndDoctorId(patientId, doctorId));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (ResourceNotFoundException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
        }
    }
}