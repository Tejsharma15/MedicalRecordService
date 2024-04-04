package com.example.EMR.controller;

import com.example.EMR.dto.ConsultationDto;
import com.example.EMR.models.Consultation;
import com.example.EMR.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?>addConsultation(@RequestBody ConsultationDto consultationdto){
        System.out.println("Adding consultation");
        return consultationService.addConsultation(consultationdto);
    }

    @GetMapping("/getAllConsultations")
    public List<Consultation> getAllConsultations(){
        return consultationService.getAllConsultations();
    }
}