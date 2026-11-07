package com.healthflow.api.controller;

import com.healthflow.service.StaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/staff/{id}")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping("/leave") //Durum kodları döndürmek için responseentity classıyla yapmak daha iyi burada düz void de yapabilirdik ama http status code önemli.
    public ResponseEntity<Void> startLeave(@PathVariable UUID id) {
        staffService.startLeave(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/return")
    public ResponseEntity<Void> returnFromLeave(@PathVariable UUID id) {
        staffService.returnFromLeave(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/return")
    public ResponseEntity<Void> suspend(@PathVariable UUID id) {
        staffService.suspend(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/return")
    public ResponseEntity<Void> reinstate(@PathVariable UUID id) {
        staffService.reinstate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/return")
    public ResponseEntity<Void> terminate(@PathVariable UUID id) {
        staffService.terminate(id);
        return ResponseEntity.noContent().build();
    }

}
