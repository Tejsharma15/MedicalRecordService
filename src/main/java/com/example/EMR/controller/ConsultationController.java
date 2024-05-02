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
        UUID privateId = null;
        try{
            privateId = publicPrivateService.privateIdByPublicId(consultationDto.getPatientId());
            if(privateId == null){
                logService.addLog("ERROR", "POST: Adding a consultation, ", null, privateId);
                return new ResponseEntity<>("Could not Add consultation for patient id:"+ consultationDto.getPatientId(), HttpStatus.NOT_FOUND);
            }
            logService.addLog("INFO", "POST: Adding consultation", null, privateId);
            return consultationService.addConsultation(consultationDto);
        }catch (Exception e){
            if(privateId!=null)
                logService.addLog("ERROR", "POST: Adding a consultation, " + e, null, privateId);
            return new ResponseEntity<>("Could not Add consultation for patient id:" + consultationDto.getPatientId(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getAllConsultations")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<List<ConsultationRequestDto>> getAllConsultations(){
        logService.addLog("INFO", "GET: All consultation", null, null);
        return consultationService.getAllConsultations();
    }

    @GetMapping("/getConsultationById/{consultationId}")
    @PreAuthorize(("hasAuthority('admin:read')"))
    public ResponseEntity<?> getConsultationById(@PathVariable ("consultationId") String consultationId){
        UUID privateId = null;
        try{
            privateId = publicPrivateService.privateIdByPublicId(consultationId);
            if(privateId == null){
                logService.addLog("ERROR", "GET: Consultation by ID, ", null, privateId);
                return new ResponseEntity<>("Could not find consultation with id: " + consultationId, HttpStatus.NOT_FOUND);
            }
            logService.addLog("INFO", "GET: Consultation by Id", null, privateId);
            return consultationService.getConsultationById(consultationId);
        }catch (Exception e){
            if(privateId!=null)
                logService.addLog("ERROR", "GET: Consultation by ID, " + e, null, privateId);
            return new ResponseEntity<>("Could not find consultation with id: " + consultationId, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getEmrIdByPatientIdAndDoctorId")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<?> getEmrIdByPatientIdAndDoctorId(@RequestParam String patientId, @RequestParam String doctorId){
        UUID patientPvtId = null;
        UUID doctorPvtId = null;
        try{
            patientPvtId = publicPrivateService.privateIdByPublicId(patientId);
            doctorPvtId = publicPrivateService.privateIdByPublicId(doctorId);
            if(patientPvtId == null){
                logService.addLog("ERROR", "GET: EMR by Patient and Doctor, ", null, patientPvtId);
                return new ResponseEntity<>("Could not find EMR by doctor and patient"+ patientId +" "+doctorId, HttpStatus.NOT_FOUND);
            }
            logService.addLog("INFO", "GET: EMR by Patient and Doctor", null, null);
            return ResponseEntity.ok(consultationService.getEmrIdByPatientIdAndDoctorId(patientPvtId, doctorPvtId));
        }catch (Exception e){
            if(patientPvtId!=null && doctorPvtId != null)
                logService.addLog("ERROR", "GET: EMR by Patient and Doctor, " + e, null, patientPvtId);
            return new ResponseEntity<>("Could not find EMR by doctor and patient"+ patientId +" "+doctorId, HttpStatus.NOT_FOUND);
        }
    }
}