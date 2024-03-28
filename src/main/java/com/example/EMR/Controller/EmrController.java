package com.example.EMR.Controller;

import com.example.EMR.DTO.EmrDto;
import com.example.EMR.DTO.UpdateEmrDto;
import com.example.EMR.Service.EmrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
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
    public ResponseEntity<InputStreamResource> getPrescriptionsEmrId(@PathVariable("emrId") UUID emrId) throws FileNotFoundException{
        System.out.println("Returning EMR BY ID");
        return emrService.getPrescriptionByEmrId(emrId);
    }

    @GetMapping("/getCommentsByEmrId/{emrId}")
    public ResponseEntity<InputStreamResource> getCommentsByEmrId(@PathVariable("emrId") UUID emrId) throws FileNotFoundException {
        System.out.println("Returning EMR BY ID");
        return emrService.getCommentsByEmrId(emrId);
    }


    @PostMapping("/insertNewEmr")
    public ResponseEntity<?> insertNewEmr(@ModelAttribute EmrDto emrDto){
        System.out.println("Inserting a new emr record provided");
        return emrService.insertNewEmr(emrDto);
    }

    @PutMapping("/updateEmrById")
    public ResponseEntity<?> updateEmrById(@ModelAttribute UpdateEmrDto updateEmrDto){
        System.out.println("Updating emr by id");
        return emrService.updateEmrById(updateEmrDto);
    }

    @PutMapping("/deleteEmrByPatientId/{patientId}")
    public ResponseEntity<?> deleteEmrByPatientId(@PathVariable("patientId") UUID patientId){
        System.out.println("Deleting emr by id");
        return emrService.deleteEmrByPatientId(patientId);
    }
}

