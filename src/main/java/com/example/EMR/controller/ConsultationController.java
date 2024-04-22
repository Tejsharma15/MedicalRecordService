package com.example.EMR.controller;

import com.example.EMR.Exception.ResourceNotFoundException;
import com.example.EMR.dto.ConsultationDto;
import com.example.EMR.dto.ConsultationRequestDto;
import com.example.EMR.logging.LogService;
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
    private LogService logService;
    private final PublicPrivateService publicPrivateService;

    @Autowired
    public ConsultationController(ConsultationService consultationService, LogService logService, PublicPrivateService publicPrivateService){
        this.consultationService=consultationService;
        this.logService = logService;
        this.publicPrivateService = publicPrivateService;
    }

    @PostMapping("/addConsultation")
    @PreAuthorize("hasAuthority('admin:create') or hasAuthority('desk:create')")
    public ResponseEntity<?>addConsultation(@RequestBody ConsultationDto consultationDto) throws ResourceNotFoundException, NoSuchAlgorithmException {
        System.out.println("Adding consultation");
        logService.addLog("INFO", "POST: Adding consultation", null, publicPrivateService.privateIdByPublicId(consultationDto.getPatientId()));
        return consultationService.addConsultation(consultationDto);
    }

    @GetMapping("/getAllConsultations")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<List<ConsultationRequestDto>> getAllConsultations(){
        logService.addLog("INFO", "GET: All consultation", null, null);
        return consultationService.getAllConsultations();
    }

    @GetMapping("/getConsultationById/{consultationId}")
    @PreAuthorize(("hasAuthority('admin:read')"))
    public ResponseEntity<ConsultationRequestDto> getConsultationById(@PathVariable ("consultationId") UUID consultationId){
        logService.addLog("INFO", "GET: Consultation by Id", null, consultationService.getPatientIdByConsultationId(consultationId));
        return consultationService.getConsultationById(consultationId);
    }

    @GetMapping("/getEmrIdByPatientIdAndDoctorId")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<?> getEmrIdByPatientIdAndDoctorId(@RequestParam String patientId, @RequestParam String doctorId){
        UUID patientPvtId = publicPrivateService.privateIdByPublicId(patientId);
        UUID doctorPvtId = publicPrivateService.privateIdByPublicId(doctorId);
        logService.addLog("INFO", "GET: EMR by Patient and Doctor", null, null);
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