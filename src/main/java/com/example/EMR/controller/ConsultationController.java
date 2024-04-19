package com.example.EMR.controller;

import com.example.EMR.Exception.ResourceNotFoundException;
import com.example.EMR.dto.ConsultationDto;
import com.example.EMR.dto.ConsultationRequestDto;
import com.example.EMR.service.ConsultationService;
import com.example.EMR.service.PublicPrivateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/consultation")
public class ConsultationController {
    private ConsultationService consultationService;
    private final PublicPrivateService publicPrivateService;
    private static final Logger logger = LogManager.getLogger("com.example");

    @Autowired
    public ConsultationController(ConsultationService consultationService, PublicPrivateService publicPrivateService){
        this.consultationService=consultationService;
        this.publicPrivateService = publicPrivateService;
    }

    @PostMapping("/addConsultation")
    @PreAuthorize("hasAuthority('admin:create') or hasAuthority('desk:create')")
    public ResponseEntity<?>addConsultation(@RequestBody ConsultationDto consultationDto) throws ResourceNotFoundException, NoSuchAlgorithmException {
        System.out.println("Adding consultation");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String actor = authentication.getName();
        UUID object = publicPrivateService.privateIdByPublicId(consultationDto.getPatientId());
        ThreadContext.put("actorUUID", actor);
        ThreadContext.put("userUUID", object.toString());
        System.out.println(ThreadContext.get("actorUUID"));
        System.out.println(ThreadContext.get("userUUID"));
        logger.info("POST: Adding consultation");
        ThreadContext.clearAll();
        return consultationService.addConsultation(consultationDto);
    }

    @GetMapping("/getAllConsultations")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<List<ConsultationRequestDto>> getAllConsultations(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String actor = authentication.getName();
        ThreadContext.put("actorUUID", actor);
        System.out.println(ThreadContext.get("actorUUID"));
        logger.info("GET: All consultations");
        ThreadContext.clearAll();
        return consultationService.getAllConsultations();
    }

    @GetMapping("/getConsultationById/{consultationId}")
    @PreAuthorize(("hasAuthority('admin:read')"))
    public ResponseEntity<ConsultationRequestDto> getConsultationById(@PathVariable ("consultationId") UUID consultationId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String actor = authentication.getName();
        UUID object = consultationService.getPatientIdByConsultationId(consultationId);
        ThreadContext.put("actorUUID", actor);
        ThreadContext.put("userUUID", object.toString());
        System.out.println(ThreadContext.get("actorUUID"));
        System.out.println(ThreadContext.get("userUUID"));
        logger.info("GET: Consultation ID");
        ThreadContext.clearAll();
        return consultationService.getConsultationById(consultationId);
    }

    @GetMapping("/getEmrIdByPatientIdAndDoctorId")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<?> getEmrIdByPatientIdAndDoctorId(@RequestParam String patientId, @RequestParam String doctorId){
        UUID patientPvtId = publicPrivateService.privateIdByPublicId(patientId);
        UUID doctorPvtId = publicPrivateService.privateIdByPublicId(doctorId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String actor = authentication.getName();
        ThreadContext.put("actorUUID", actor);
        ThreadContext.put("userUUID", patientPvtId.toString());
        System.out.println(ThreadContext.get("actorUUID"));
        System.out.println(ThreadContext.get("userUUID"));
        logger.info("GET: EMR-ID from patient and doctor id");
        ThreadContext.clearAll();
        try {
            return ResponseEntity.ok(consultationService.getEmrIdByPatientIdAndDoctorId(patientPvtId, doctorPvtId));
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