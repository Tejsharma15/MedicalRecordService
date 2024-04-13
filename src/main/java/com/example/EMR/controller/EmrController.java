package com.example.EMR.controller;

import com.example.EMR.dto.EmrDto;
import com.example.EMR.dto.UpdateEmrDtoText;
import com.example.EMR.service.EmrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@RestController
@RequestMapping("/emr")
public class EmrController {
    private final EmrService emrService;
    @Autowired
    EmrController(EmrService emrService){
        this.emrService = emrService;
    }

    @GetMapping("/getPrescriptionByEmrId/{emrId}")
    @PreAuthorize("hasAuthority('prescription:read') or hasAuthority('patient:read')")
    public ResponseEntity<InputStreamResource> getPrescriptionsEmrId(@PathVariable("emrId") UUID emrId) throws FileNotFoundException{
        System.out.println("Returning Prescription BY ID");
        return emrService.getPrescriptionByEmrId(emrId);
    }

    @GetMapping("/getCommentsByEmrId/{emrId}")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<InputStreamResource> getCommentsByEmrId(@PathVariable("emrId") UUID emrId) throws FileNotFoundException {
        System.out.println("Returning Comments BY ID");
        return emrService.getCommentsByEmrId(emrId);
    }

    @GetMapping("/getEmrByPatientId/{patientId}")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<byte[]> getEmrByPatientId(@PathVariable("patientId") UUID patientId) throws FileNotFoundException{
        System.out.println("Returning EMR");
        return emrService.getEmrByPatientId(patientId);
    }
    @PostMapping("/insertNewEmr")
    @PreAuthorize("hasAuthority('patient:write')")
    public ResponseEntity<?> insertNewEmr(@ModelAttribute EmrDto emrDto){
        System.out.println("Inserting a new emr record provided");
        return emrService.insertNewEmr(emrDto);
    }

    @PutMapping("/updateEmrById")
    @PreAuthorize("hasAuthority('patient:update')")
    public ResponseEntity<?> updateEmrById(@ModelAttribute UpdateEmrDtoText updateEmrDtoText) throws NoSuchAlgorithmException {
        System.out.println("Updating emr by id");
        return emrService.updateEmrByIdText(updateEmrDtoText);
    }

    @PutMapping("/deleteEmrByPatientId/{patientId}")
    @PreAuthorize("hasAuthority('patient:delete')")
    public ResponseEntity<?> deleteEmrByPatientId(@PathVariable("patientId") UUID patientId){
        System.out.println("Deleting emr by id");
        return emrService.deleteEmrByPatientId(patientId);
    }
}

