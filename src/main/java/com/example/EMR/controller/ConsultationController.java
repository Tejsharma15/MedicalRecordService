package com.example.EMR.controller;

import com.example.EMR.Exception.ResourceNotFoundException;
import com.example.EMR.dto.ConsultationDto;
import com.example.EMR.dto.ConsultationRequestDto;
import com.example.EMR.dto.UpdateConsultationDto;
import com.example.EMR.dto.UpdateSeverityStatusDto;
import com.example.EMR.logging.LogService;
import com.example.EMR.service.ConsultationService;
import com.example.EMR.service.PatientService;
import com.example.EMR.service.PublicPrivateService;
import com.example.EMR.service.UserService;
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
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/consultation")
public class ConsultationController {
    private ConsultationService consultationService;
    private LogService logService;
    private final PublicPrivateService publicPrivateService;

    private final PatientService patientService;
    private final UserService userService;

    @Autowired
    public ConsultationController(ConsultationService consultationService, LogService logService, PublicPrivateService publicPrivateService, PatientService patientService, UserService userService){
        this.consultationService=consultationService;
        this.logService = logService;
        this.publicPrivateService = publicPrivateService;
        this.patientService = patientService;
        this.userService = userService;
    }

    @PostMapping("/addConsultation")
    @PreAuthorize("hasAuthority('admin:create') or hasAuthority('desk:create')")
    public ResponseEntity<?>addConsultation(@RequestBody ConsultationDto consultationDto) throws ResourceNotFoundException, NoSuchAlgorithmException {
        System.out.println("Adding consultation");
        UUID privateId = null;
        UUID doctorId = null;
        try{
            privateId = publicPrivateService.privateIdByPublicId(consultationDto.getPatientId());
            doctorId = publicPrivateService.privateIdByPublicId(consultationDto.getDoctorId());
            if(patientService.verifyPatient(privateId))    return new ResponseEntity<>("No access given for the user. Patient deleted/not verified", HttpStatus.OK);
            if(userService.verifyUser(doctorId))    return new ResponseEntity<>("Not possible. Doctor deleted", HttpStatus.BAD_REQUEST);
            if(privateId == null){
                logService.addLog("ERROR", "POST: Adding a consultation, ", null, privateId);
                return new ResponseEntity<>("Could not Add consultation for patient id:"+ consultationDto.getPatientId(), HttpStatus.NOT_FOUND);
            }
            logService.addLog("APP", "Added a new consultation", null, privateId);
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
        logService.addLog("APP", "Viewed all consultations", null, null);
        return consultationService.getAllConsultations();
    }

    @GetMapping("/getConsultationById/{consultationId}")
    @PreAuthorize(("hasAuthority('admin:read')"))
    public ResponseEntity<?> getConsultationById(@PathVariable ("consultationId") String consultationId){
        UUID privateId = null;
        try{
            privateId = publicPrivateService.privateIdByPublicId(consultationId);
            if(patientService.verifyPatient(privateId))    return new ResponseEntity<>("No access given for the user. Patient deleted/not verified", HttpStatus.OK);
            if(privateId == null){
                logService.addLog("ERROR", "GET: Consultation by ID, ", null, privateId);
                return new ResponseEntity<>("Could not find consultation with id: " + consultationId, HttpStatus.NOT_FOUND);
            }
            logService.addLog("APP", "Viewed consultation", null, privateId);
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
            if(patientService.verifyPatient(patientPvtId))    return new ResponseEntity<>("No access given for the user. Patient deleted/not verified", HttpStatus.OK);
            doctorPvtId = publicPrivateService.privateIdByPublicId(doctorId);
            if(userService.verifyUser(doctorPvtId))    return new ResponseEntity<>("Not possible. Doctor deleted", HttpStatus.BAD_REQUEST);
            if(patientPvtId == null){
                logService.addLog("ERROR", "GET: EMR by Patient and Doctor, ", null, patientPvtId);
                return new ResponseEntity<>("Could not find EMR by doctor and patient"+ patientId +" "+doctorId, HttpStatus.NOT_FOUND);
            }
            logService.addLog("APP", "Accessed EMR ID for consultation", null, null);
            return ResponseEntity.ok(consultationService.getEmrIdByPatientIdAndDoctorId(patientPvtId, doctorPvtId));
        }catch (Exception e){
            if(patientPvtId!=null && doctorPvtId != null)
                logService.addLog("ERROR", "GET: EMR by Patient and Doctor, " + e, null, patientPvtId);
            return new ResponseEntity<>("Could not find EMR by doctor and patient"+ patientId +" "+doctorId, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/reassignDoctor")
    @PreAuthorize("hasAuthority('desk:update')")
    public ResponseEntity<?> reassignDoctor(@RequestBody UpdateConsultationDto updateConsultationDto){
        System.out.println("Updating consultation");
        UUID privateId = null;
        UUID doctorId = null;
        try{
            privateId = publicPrivateService.privateIdByPublicId(updateConsultationDto.getPatientId());
            doctorId = publicPrivateService.privateIdByPublicId(updateConsultationDto.getNewDoctorId());
            if(patientService.verifyPatient(privateId))    return new ResponseEntity<>("No access given for the user. Patient deleted/not verified", HttpStatus.OK);
            if(userService.verifyUser(doctorId))    return new ResponseEntity<>("Not possible. Doctor deleted", HttpStatus.BAD_REQUEST);
            if(privateId == null){
                logService.addLog("ERROR", "PUT: Updating a consultation, ", null, privateId);
                return new ResponseEntity<>("Could not Update consultation for patient id:"+ updateConsultationDto.getPatientId(), HttpStatus.NOT_FOUND);
            }
            logService.addLog("APP", "Updated consultation", null, privateId);
            return consultationService.updateConsultation(updateConsultationDto);
        }catch (Exception e){
            if(privateId!=null)
                logService.addLog("ERROR", "POST: update a consultation, " + e, null, privateId);
            return new ResponseEntity<>("Could not update consultation for patient id:" + updateConsultationDto.getPatientId(), HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/updateSeverity")
    @PreAuthorize("hasAuthority('patient:update')")
    public ResponseEntity<?> updateSeverity(@RequestBody UpdateSeverityStatusDto updateSeverityStatusDto){
        System.out.println("Updating patient severity");
        UUID privateId = null;
        try{
            privateId = publicPrivateService.privateIdByPublicId(updateSeverityStatusDto.getConsultationId());
            if(privateId == null){
                logService.addLog("ERROR", "PUT: Update Severity", null, privateId);
                return new ResponseEntity<>("Could not update severity for this consultation", HttpStatus.BAD_REQUEST);
            }
            logService.addLog("APP", "Updated patient status", null, privateId);
            return consultationService.updateSeverity(updateSeverityStatusDto);
        } catch(Exception e){
            if(updateSeverityStatusDto.getConsultationId() != null){
                logService.addLog("ERROR", "PUT: Update severity"+e, null, privateId);
            }
            return new ResponseEntity<>("Could not update severity for : "+updateSeverityStatusDto.getConsultationId(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getSeverity/{consultationId}")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<?> getSeverity(@PathVariable ("consultationId") String consultationId){
        System.out.println("Getting patient severity");
        UUID privateId = null;
        try{
            privateId = publicPrivateService.privateIdByPublicId(consultationId);
            if(privateId == null){
                logService.addLog("ERROR", "PUT: Update Severity", null, privateId);
                return new ResponseEntity<>("Could not update severity for this consultation", HttpStatus.BAD_REQUEST);
            }
            logService.addLog("APP", "Viewed Patient Status", null, privateId);
            return consultationService.getSeverity(privateId);
        } catch(Exception e){
            if(consultationId != null){
                logService.addLog("ERROR", "PUT: Update severity"+e, null, privateId);
            }
            return new ResponseEntity<>("Could not update severity for : "+consultationId, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllSeverity")
    @PreAuthorize("hasAuthority('patient:read')")
    public ResponseEntity<?> getAllSeverity() {
        System.out.println("Getting all patient severities");
        try {
            Map<String, String> consultationSeverities = consultationService.getAllConsultationsWithSeverity();
            // Log successful retrieval of severities if needed
            logService.addLog("APP", "Retrieved all patient severities", null, null);
            return ResponseEntity.ok(consultationSeverities);
        } catch (Exception e) {
            // Log any errors that occur during retrieval
            System.out.println("Error occurred while retrieving severities: " + e.getMessage());
            logService.addLog("ERROR", "Error occurred while retrieving severities: " + e.getMessage(), null, null);
            return new ResponseEntity<>("Could not retrieve severities", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}