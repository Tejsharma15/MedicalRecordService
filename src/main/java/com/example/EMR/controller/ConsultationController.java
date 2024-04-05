package com.example.EMR.controller;

import com.example.EMR.dto.ConsultationDto;
import com.example.EMR.models.Consultation;
import com.example.EMR.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?>addConsultation(@RequestBody ConsultationDto consultationdto){
        System.out.println("Adding consultation");
        return consultationService.addConsultation(consultationdto);
    }

    @GetMapping("/getAllConsultations")
    @PreAuthorize("hasAuthority('admin:read')")
    public List<Consultation> getAllConsultations(){
        return consultationService.getAllConsultations();
    }
}