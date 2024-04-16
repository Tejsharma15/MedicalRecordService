package com.example.EMR.controller;

import com.example.EMR.Exception.ResourceNotFoundException;
import com.example.EMR.dto.ConsultationDto;
import com.example.EMR.dto.ConsultationRequestDto;
import com.example.EMR.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/consultation")
public class ConsultationController {
    @Autowired
    private ConsultationService consultationService;


    public ConsultationController(ConsultationService consultationService){
        this.consultationService=consultationService;
    }

    @PostMapping("/addConsultation")
    @PreAuthorize("hasAuthority('admin:create') or hasAuthority('desk:create')")
    public ResponseEntity<?>addConsultation(@RequestBody ConsultationDto consultationdto) throws ResourceNotFoundException {
        System.out.println("Adding consultation");
        return consultationService.addConsultation(consultationdto);
    }

    @GetMapping("/getAllConsultations")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<List<ConsultationRequestDto>> getAllConsultations(){
        return consultationService.getAllConsultations();
    }

    @GetMapping("/getConsultationById/{consultationId}")
    @PreAuthorize(("hasAuthority('admin:read')"))
    public ResponseEntity<ConsultationRequestDto> getConsultationById(@PathVariable ("consultationId") UUID consultationId){
        return consultationService.getConsultationById(consultationId);
    }

    @GetMapping("/getEmrIdByPatientIdAndDoctorId")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<?> getEmrIdByPatientIdAndDoctorId(@RequestParam UUID patientId, @RequestParam UUID doctorId){
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