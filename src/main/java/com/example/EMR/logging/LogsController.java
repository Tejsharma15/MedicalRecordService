package com.example.EMR.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/logs")
public class LogsController {
    @Autowired
    private LogService logService;

    @GetMapping("/getLogsByActorId")
    public ResponseEntity<?>getLogsByActorId(){
        try{
            return ResponseEntity.ok(logService.getLogsByActorId());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getLogsByUserId/{userId}")
    public ResponseEntity<?>getLogsByUserId(@PathVariable String userId){
        try{

            return ResponseEntity.ok(logService.getLogsByUserId(userId));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/getAllLogs")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<?>getAllLogs(){
        try{
            return ResponseEntity.ok(logService.getAllLogs());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
