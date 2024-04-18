package com.example.EMR.controller;

import com.example.EMR.configuration.JwtService;
import com.example.EMR.dto.EmrDto;
import com.example.EMR.dto.UpdateEmrDtoText;
import com.example.EMR.service.EmrService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/emr")
public class EmrController {
    private final EmrService emrService;
    private final JwtService jwtService;
    private static final Logger logger = LogManager.getLogger("com.example");
    @Autowired
    EmrController(EmrService emrService, JwtService jwtService){
        this.emrService = emrService;
        this.jwtService = jwtService;
    }

    @GetMapping("/getPrescriptionByEmrId/{emrId}")
    @PreAuthorize("hasAuthority('prescription:read') or hasAuthority('patient:read')")
    public ResponseEntity<?> getPrescriptionsEmrId(@PathVariable("emrId") UUID emrId) throws FileNotFoundException{
        System.out.println("Returning Prescription BY ID");
        String username = UUID.randomUUID().toString();
        ThreadContext.put("actorUUID", username);
        System.out.println(ThreadContext.get("actorUUID"));
        logger.info("Returning Prescription BY ID");
        ThreadContext.clearAll();
        return emrService.getPrescriptionByEmrId(emrId);
    }

    @GetMapping("/getCommentsByEmrId/{emrId}")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<?> getCommentsByEmrId(@PathVariable("emrId") UUID emrId) throws FileNotFoundException {
        System.out.println("Returning Comments BY ID");
        String username = UUID.randomUUID().toString();
        ThreadContext.put("actorUUID", username);
        System.out.println(ThreadContext.get("actorUUID"));
        logger.info("Returning Comments BY ID");
        ThreadContext.clearAll();
        return emrService.getCommentsByEmrId(emrId);
    }

    @GetMapping("/getEmrByPatientIdText/{patientId}")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<?> getEmrByPatientIdText(@PathVariable("patientId") UUID patientId) throws IOException {
        System.out.println("Returning EMR");
        String username = UUID.randomUUID().toString();
        ThreadContext.put("actorUUID", username);
        System.out.println(ThreadContext.get("actorUUID"));
        logger.info("Returning EMR by Patient ID");
        ThreadContext.clearAll();
        try {
            return emrService.getEmrByPatientIdText(patientId);
        } catch (FileNotFoundException e) {
            // Handle file not found error
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "File not found"));
        } catch (IOException e) {
            // Handle other IO exceptions
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "File not found"));
        }
    }

    @GetMapping("/getEmrByEmrIdText/{emrId}")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<?> getEmrByPublicEmrIdText(@PathVariable("emrId") UUID emrId) throws IOException {
        System.out.println("Returning EMR");
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
        
        String username = UUID.randomUUID().toString();
        ThreadContext.put("actorUUID", username);
        System.out.println(ThreadContext.get("actorUUID"));
        logger.info("Returning EMR by EMR ID");
        ThreadContext.clearAll();
        try {
            return emrService.getEmrByEmrIdText(emrId);
        } catch (FileNotFoundException e) {
            // Handle file not found error
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "File not found"));
        } catch (IOException e) {
            // Handle other IO exceptions
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "File not found"));
        }
    }

    @PostMapping("/insertNewEmr")
    @PreAuthorize("hasAuthority('patient:write')")
    public ResponseEntity<?> insertNewEmr(@ModelAttribute EmrDto emrDto){
        System.out.println("Inserting a new emr record provided");
        String username = UUID.randomUUID().toString();
        ThreadContext.put("actorUUID", username);
        System.out.println(ThreadContext.get("actorUUID"));
        logger.info("Inserting a new emr record provided");
        ThreadContext.clearAll();
        return emrService.insertNewEmr(emrDto);
    }

    @PutMapping("/updateEmrByIdText")
    @PreAuthorize("hasAuthority('patient:update')")
    public ResponseEntity<?> updateEmrByIdText(@ModelAttribute UpdateEmrDtoText updateEmrDtoText) throws NoSuchAlgorithmException {
        System.out.println("Updating emr by id");
        String username = UUID.randomUUID().toString();
        ThreadContext.put("actorUUID", username);
        System.out.println(ThreadContext.get("actorUUID"));
        logger.info("Updating emr by id");
        ThreadContext.clearAll();
        return emrService.updateEmrByIdText(updateEmrDtoText);
    }

    @PutMapping("/deleteEmrByPatientId/{patientId}")
    @PreAuthorize("hasAuthority('patient:delete')")
    public ResponseEntity<?> deleteEmrByPatientId(@PathVariable("patientId") UUID patientId){
        System.out.println("Deleting emr by id");
        String username = UUID.randomUUID().toString();
        ThreadContext.put("actorUUID", username);
        System.out.println(ThreadContext.get("actorUUID"));
        logger.info("Deleting emr by id");
        ThreadContext.clearAll();
        return emrService.deleteEmrByPatientId(patientId);
    }
}

