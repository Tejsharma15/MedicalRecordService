package com.example.EMR.controller;

import com.example.EMR.configuration.JwtService;
import com.example.EMR.dto.UpdateEmrDtoText;
import com.example.EMR.logging.LogService;
import com.example.EMR.service.EmrService;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/emr")
public class EmrController {
    private final EmrService emrService;
    private final LogService logService;
    private final PublicPrivateService publicPrivateService;
    private static final Logger logger = LogManager.getLogger("com.example");
    @Autowired
    EmrController(EmrService emrService, LogService logService, PublicPrivateService publicPrivateService){
        this.emrService = emrService;
        this.logService = logService;
        this.publicPrivateService = publicPrivateService;
    }

    @GetMapping("/getPrescriptionByEmrId/{emrId}")
    @PreAuthorize("hasAuthority('prescription:read') or hasAuthority('patient:read')")
    public ResponseEntity<?> getPrescriptionsEmrIdText(@PathVariable("emrId") String emrId) throws FileNotFoundException{
        System.out.println("Returning Prescription BY ID");
        logService.addLog("INFO", "GET: Prescription by EMR ID", null, emrService.getPatientIdByEmrId(emrId));
        return emrService.getPrescriptionByEmrIdText(emrId);
    }

    @GetMapping("/getCommentsByEmrId/{emrId}")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<?> getCommentsByEmrIdText(@PathVariable("emrId") String emrId) throws FileNotFoundException {
        System.out.println("Returning Comments BY ID");
        logService.addLog("INFO", "GET: Comments by EMR ID", null, emrService.getPatientIdByEmrId(emrId));
        return emrService.getCommentsByEmrIdText(emrId);
    }

    @GetMapping("/getEmrByPatientIdText/{patientId}")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<?> getEmrByPatientIdText(@PathVariable("patientId") String patientId) throws IOException {
        System.out.println("Returning EMR");
        logService.addLog("INFO", "GET: EMR by Patient ID", null, publicPrivateService.privateIdByPublicId(patientId));
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
    public ResponseEntity<?> getEmrByPublicEmrIdText(@PathVariable("emrId") String emrId) throws IOException {
        System.out.println("Returning EMR");
        logService.addLog("INFO", "GET: EMR by Public EMR ID", null, emrService.getPatientIdByEmrId(emrId));
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

//    @PostMapping("/insertNewEmr")
//    @PreAuthorize("hasAuthority('patient:write')")
//    public ResponseEntity<?> insertNewEmr(@ModelAttribute EmrDto emrDto){
//        System.out.println("Inserting a new emr record provided");
//        String username = UUID.randomUUID().toString();
//        ThreadContext.put("actorUUID", username);
//        System.out.println(ThreadContext.get("actorUUID"));
//        logger.info("Inserting a new emr record provided");
//        ThreadContext.clearAll();
//        return emrService.insertNewEmr(emrDto);
//    }

    @PutMapping("/updateEmrByIdText")
    @PreAuthorize("hasAuthority('patient:update')")
    public ResponseEntity<?> updateEmrByIdText(@ModelAttribute UpdateEmrDtoText updateEmrDtoText) throws NoSuchAlgorithmException {
        System.out.println("Updating emr by id");
        logService.addLog("INFO", "PUT: Update by EMR ID", null, emrService.getPatientIdByEmrId(updateEmrDtoText.getPublicEmrId()));
        return emrService.updateEmrByIdText(updateEmrDtoText);
    }

    @PutMapping("/deleteEmrByPatientId/{patientId}")
    @PreAuthorize("hasAuthority('patient:delete')")
    public ResponseEntity<?> deleteEmrByPatientId(@PathVariable("patientId") String patientId){
        System.out.println("Deleting emr by id");
        logService.addLog("INFO", "PUT: Delete by EMR ID", null, publicPrivateService.privateIdByPublicId(patientId));
        return emrService.deleteEmrByPatientId(patientId);
    }
}

