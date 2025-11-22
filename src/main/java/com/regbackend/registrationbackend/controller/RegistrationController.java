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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    // ✅ Register a new user
    @PostMapping("/register")
    public ResponseEntity<APIModel<RegistrationEntity>> registerUser(@Valid @RequestBody RegistrationModel registrationModel) {
        try {
            RegistrationEntity saved = registrationService.registerUser(registrationModel);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new APIModel<>(201, "User registered successfully", saved));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIModel<>(400, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIModel<>(500, "An unexpected error occurred: " + ex.getMessage(), null));
        }
    }

    // ✅ Get all registrations
    @GetMapping
    public ResponseEntity<APIModel<List<RegistrationEntity>>> getAllRegistrations() {
        List<RegistrationEntity> list = registrationService.getAllRegistrations();
        return ResponseEntity.ok(new APIModel<>(200, "All registrations fetched successfully", list));
    }

    // ✅ Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<APIModel<RegistrationEntity>> getRegistrationById(@PathVariable String id) {
        RegistrationEntity registration = registrationService.getRegistrationById(id);
        if (registration == null) throw new ResourceNotFoundException("Registration not found with ID: " + id);
        return ResponseEntity.ok(new APIModel<>(200, "Registration fetched successfully", registration));
    }

    // ✅ Update registration
    @PutMapping("/{id}")
    public ResponseEntity<APIModel<RegistrationEntity>> updateRegistration(@PathVariable String id, @RequestBody RegistrationModel registrationModel) {
        RegistrationEntity updated = registrationService.updateRegistration(id, registrationModel);
        return ResponseEntity.ok(new APIModel<>(200, "Registration updated successfully", updated));
    }

    // ✅ Delete registration
    @DeleteMapping("/{id}")
    public ResponseEntity<APIModel<String>> deleteRegistration(@PathVariable String id) {
        registrationService.deleteRegistration(id);
        return ResponseEntity.ok(new APIModel<>(200, "Registration deleted successfully", null));
    }

    // ✅ Filtered results
    @GetMapping("/filter")
    public ResponseEntity<APIModel<RegistrationFilterModel>> getByFilters(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String eventtype,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String registered_as,
            @RequestParam(required = false) String cnic,
            @RequestParam(required = false) String organizationOrUniversity,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String ambassador,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String workshopName,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        RegistrationFilterModel result = registrationService.getByFilters(
                status, name, eventtype, gender, registered_as, cnic, organizationOrUniversity,phoneNumber, ambassador,email,workshopName, pageNumber, pageSize
        );

        if (result.getRegistrations().isEmpty()) {
            return ResponseEntity.ok(new APIModel<>(404, "Data not found", result));
        }

        return ResponseEntity.ok(new APIModel<>(200, "Filtered data fetched successfully", result));
    }


    // ✅ Stats
    @GetMapping("/stats")
    public ResponseEntity<APIModel<RegistrationStatsModel>> getStats() {
        RegistrationStatsModel stats = registrationService.getRegistrationStats();
        return ResponseEntity.ok(new APIModel<>(200, "Statistics fetched successfully", stats));
    }

    @PutMapping("/update-status")
    public ResponseEntity<APIModel<String>> updateStatusBulk(@RequestBody BulkStatusUpdateModel model) {
        try {
            registrationService.updateStatuses(model.getIds(), model.getStatus());
            return ResponseEntity.ok(new APIModel<>(200, "Statuses updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIModel<>(500, "Error updating statuses: " + e.getMessage(), null));
        }
    }


    @PostMapping("/scanQR")
    public ResponseEntity<APIModel<Map<String, Object>>> scanQRAndUpdateStatus(
            @RequestBody Map<String, Object> requestBody
    ) {
        try {
            String publicId = requestBody.get("publicId").toString(); // ✅ now using publicId
            String status = requestBody.get("status").toString();

            Map<String, Object> response = registrationService.scanQRAndUpdateStatus(publicId, status);

            return ResponseEntity.ok(
                    new APIModel<>(200, response.get("message").toString(), response)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIModel<>(500, "Error updating status: " + e.getMessage(), null));
        }
    }


}
