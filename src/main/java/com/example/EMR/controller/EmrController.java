package com.example.EMR.controller;

import com.example.EMR.Exception.ResourceNotFoundException;
import com.example.EMR.dto.UpdateEmrDto;
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
        UUID privateId = null;
        try{
            privateId = emrService.getPatientIdByEmrId(emrId);
            if(privateId == null){
                logService.addLog("ERROR", "GET: Prescription by EMR ID, ", null, privateId);
                return new ResponseEntity<>("Could not find Prescription by EMR ID"+emrId, HttpStatus.NOT_FOUND);
            }
            logService.addLog("INFO", "GET: Prescription by EMR ID", null, privateId);
            return emrService.getPrescriptionByEmrIdText(emrId);
        }catch(Exception e){
            if(privateId != null){
                logService.addLog("ERROR", "GET: Prescription by EMR ID, "+e, null, privateId);
            }
            return new ResponseEntity<>("Could not find Prescription by EMR ID"+emrId, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getCommentsByEmrId/{emrId}")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<?> getCommentsByEmrIdText(@PathVariable("emrId") String emrId) throws FileNotFoundException {
        System.out.println("Returning Comments BY ID");
        UUID privateId = null;
        try{
            privateId = emrService.getPatientIdByEmrId(emrId);
            if(privateId == null){
                logService.addLog("ERROR", "GET: Comments by EMR ID, ", null, privateId);
                return new ResponseEntity<>("Could not find Comments by EMR ID"+emrId, HttpStatus.NOT_FOUND);
            }
            logService.addLog("INFO", "GET: Comments by EMR ID", null, privateId);
            return emrService.getCommentsByEmrIdText(emrId);
        }catch(Exception e){
            if(privateId != null){
                logService.addLog("ERROR", "GET: Comments by EMR ID, "+e, null, privateId);
            }
            return new ResponseEntity<>("Could not find Comments by EMR ID"+emrId, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getEmrByPatientIdText/{patientId}")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<?> getEmrByPatientIdText(@PathVariable("patientId") String patientId) throws IOException {
        System.out.println("Returning EMR");
        UUID privateId = null;
        try{
            privateId = publicPrivateService.privateIdByPublicId(patientId);
            if(privateId == null){
                logService.addLog("ERROR", "GET:EMR by Patient ID, ", null, privateId);
                return new ResponseEntity<>("Could not find Emr by Patient ID"+patientId, HttpStatus.NOT_FOUND);
            }
            logService.addLog("INFO", "GET: EMR by Patient ID", null, privateId);
            return emrService.getEmrByPatientIdText(patientId);
        }catch(Exception e){
            if(privateId != null){
                logService.addLog("ERROR", "GET: EMR by Patient ID, "+e, null, privateId);
            }
            return new ResponseEntity<>("Could not find EMR by Patient ID"+patientId, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getEmrByEmrIdText/{emrId}")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<?> getEmrByPublicEmrIdText(@PathVariable("emrId") String emrId) throws ResourceNotFoundException {
        System.out.println("Returning EMR");
        UUID privateId = null;
        try{
            privateId = emrService.getPatientIdByEmrId(emrId);
            if(privateId == null){
                logService.addLog("ERROR", "GET: EMR by Public EMR ID", null, privateId);
                return new ResponseEntity<>("Could not find EMR by Public EMR ID"+emrId, HttpStatus.NOT_FOUND);
            }
            logService.addLog("INFO", "GET: EMR by Public EMR ID", null, privateId);
            return emrService.getEmrByEmrIdText(emrId);
        }catch(Exception e){
            if(privateId != null){
                logService.addLog("ERROR", "GET: EMR by Public EMR ID, "+e, null, privateId);
            }
            return new ResponseEntity<>("Could not find EMR by Public EMR ID"+emrId, HttpStatus.NOT_FOUND);
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
        UUID privateId = null;
        try{
            privateId = emrService.getPatientIdByEmrId(updateEmrDtoText.getPublicEmrId());
            if(privateId == null){
                logService.addLog("ERROR", "PUT: Update by EMR ID, ", null, privateId);
                return new ResponseEntity<>("Could not Update by EMR ID"+updateEmrDtoText.getPublicEmrId(), HttpStatus.NOT_FOUND);
            }
            logService.addLog("INFO", "PUT: Update by EMR ID", null, privateId);
            return emrService.updateEmrByIdText(updateEmrDtoText);
        }catch(Exception e){
            if(privateId != null){
                logService.addLog("ERROR", "PUT: Update by EMR ID, "+e, null, privateId);
            }
            return new ResponseEntity<>("Could not Update by EMR ID"+updateEmrDtoText.getPublicEmrId(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/updateEmrById")
    @PreAuthorize("hasAuthority('patient:update')")
    public ResponseEntity<?> updateEmrByIdText(@ModelAttribute UpdateEmrDto updateEmrDto) throws NoSuchAlgorithmException {
        System.out.println("Updating emr by id");
        UUID privateId = null;
        try{
            privateId = emrService.getPatientIdByEmrId(updateEmrDto.getPublicEmrId());
            if(privateId == null){
                logService.addLog("ERROR", "PUT: Update by EMR ID, ", null, privateId);
                return new ResponseEntity<>("Could not Update by EMR ID"+updateEmrDto.getPublicEmrId(), HttpStatus.NOT_FOUND);
            }
            logService.addLog("INFO", "PUT: Update by EMR ID", null, privateId);
            return emrService.updateEmrById(updateEmrDto);
        }catch(Exception e){
            if(privateId != null){
                logService.addLog("ERROR", "PUT: Update by EMR ID, "+e, null, privateId);
            }
            return new ResponseEntity<>("Could not Update by EMR ID"+updateEmrDto.getPublicEmrId(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/deleteEmrByPatientId/{patientId}")
    @PreAuthorize("hasAuthority('patient:delete')")
    public ResponseEntity<?> deleteEmrByPatientId(@PathVariable("patientId") String patientId){
        System.out.println("Deleting emr by id");
        UUID privateId = null;
        try{
            privateId =  publicPrivateService.privateIdByPublicId(patientId);
            logService.addLog("INFO", "PUT: Delete by EMR ID", null, privateId);
            return emrService.deleteEmrByPatientId(patientId);
        }catch(Exception e){
            if(privateId != null){
                logService.addLog("ERROR", "GET: Prescription by EMR ID, "+e, null, privateId);
            }
            else
                logService.addLog("ERROR", "GET: Prescription by EMR ID, "+e, null, privateId);
            return new ResponseEntity<>("Could not Delete by Patient ID"+patientId, HttpStatus.NOT_FOUND);
        }
    }
}

