package com.example.EMR.controller;

import com.example.EMR.service.Patient_DoctorService;
import com.example.EMR.service.PublicPrivateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public class Patient_DoctorController {
    @Autowired
    private Patient_DoctorService patientDoctorService;
    @Autowired
    private PublicPrivateService publicPrivateService;
    @GetMapping("getAllInpatientsByDoctorID/{doctorId}")
    @PreAuthorize("hasAuthority('doctor:read')")
    public ResponseEntity<?> getAllInpatientsByDoctorID(@PathVariable String doctorId) {
        try {
            UUID privateDoctorId = publicPrivateService.privateIdByPublicId(doctorId);
            return patientDoctorService.getAllInpatientsByDoctorID(privateDoctorId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Doctor not found with id = "+doctorId, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("getAllOutpatientsByDoctorID/{doctorId}")
    @PreAuthorize("hasAuthority('doctor:read')")
    public ResponseEntity<?> getAllOutpatientsByDoctorID(@PathVariable String doctorId) {
        try {
            UUID privateDoctorId = publicPrivateService.privateIdByPublicId(doctorId);
            return patientDoctorService.getAllOutpatientsByDoctorID(privateDoctorId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Doctor not found with id = "+doctorId, HttpStatus.NOT_FOUND);
        }
    }
}
