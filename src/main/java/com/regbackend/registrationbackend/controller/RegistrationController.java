package com.regbackend.registrationbackend.controller;

import com.regbackend.registrationbackend.entity.RegistrationEntity;
import com.regbackend.registrationbackend.exception.ResourceNotFoundException;
import com.regbackend.registrationbackend.model.*;
import com.regbackend.registrationbackend.services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    // ✅ Register a new user
    @PostMapping("/register")
    public ResponseEntity<APIModel<RegistrationEntity>> registerUser(@Valid @RequestBody RegistrationModel registrationModel) {
        RegistrationEntity saved = registrationService.registerUser(registrationModel);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIModel<>(201, "User registered successfully", saved));
    }

    // ✅ Get all registrations
    @GetMapping
    public ResponseEntity<APIModel<List<RegistrationEntity>>> getAllRegistrations() {
        List<RegistrationEntity> list = registrationService.getAllRegistrations();
        return ResponseEntity.ok(new APIModel<>(200, "All registrations fetched successfully", list));
    }

    // ✅ Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<APIModel<RegistrationEntity>> getRegistrationById(@PathVariable Long id) {
        RegistrationEntity registration = registrationService.getRegistrationById(id);
        if (registration == null) throw new ResourceNotFoundException("Registration not found with ID: " + id);
        return ResponseEntity.ok(new APIModel<>(200, "Registration fetched successfully", registration));
    }

    // ✅ Update registration
    @PutMapping("/{id}")
    public ResponseEntity<APIModel<RegistrationEntity>> updateRegistration(@PathVariable Long id, @RequestBody RegistrationModel registrationModel) {
        RegistrationEntity updated = registrationService.updateRegistration(id, registrationModel);
        return ResponseEntity.ok(new APIModel<>(200, "Registration updated successfully", updated));
    }

    // ✅ Delete registration
    @DeleteMapping("/{id}")
    public ResponseEntity<APIModel<String>> deleteRegistration(@PathVariable Long id) {
        registrationService.deleteRegistration(id);
        return ResponseEntity.ok(new APIModel<>(200, "Registration deleted successfully", null));
    }

    // ✅ Filtered results
    @GetMapping("/filter")
    public ResponseEntity<APIModel<RegistrationFilterModel>> getByFilters(
            @RequestParam(required = false) String by_status,
            @RequestParam(required = false) String by_name,
            @RequestParam(required = false) String by_eventtype,
            @RequestParam(required = false) String by_gender,
            @RequestParam(required = false) String by_registered_as,
            @RequestParam(required = false) String by_cnic,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        RegistrationFilterModel result = (RegistrationFilterModel) registrationService.getByFilters(
                by_status, by_name, by_eventtype, by_gender, by_registered_as, by_cnic, pageNumber, pageSize
        );
        return ResponseEntity.ok(new APIModel<>(200, "Filtered data fetched successfully", result));
    }

    // ✅ Stats
    @GetMapping("/stats")
    public ResponseEntity<APIModel<RegistrationStatsModel>> getStats() {
        RegistrationStatsModel stats = registrationService.getRegistrationStats();
        return ResponseEntity.ok(new APIModel<>(200, "Statistics fetched successfully", stats));
    }
}
