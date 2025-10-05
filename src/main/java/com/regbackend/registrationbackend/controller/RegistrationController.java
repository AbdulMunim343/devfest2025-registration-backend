package com.regbackend.registrationbackend.controller;

import com.regbackend.registrationbackend.entity.RegistrationEntity;
import com.regbackend.registrationbackend.model.RegistrationModel;
import com.regbackend.registrationbackend.model.RegistrationResponse;
import com.regbackend.registrationbackend.services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*") // Allow frontend calls
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    // ✅ Register a new user
    @PostMapping("/register")
    public RegistrationEntity registerUser(@RequestBody RegistrationModel registrationModel) {
        return registrationService.registerUser(registrationModel);
    }

    // ✅ Get all registrations
    @GetMapping
    public List<RegistrationEntity> getAllRegistrations() {
        return registrationService.getAllRegistrations();
    }

    // ✅ Get registration by ID
    @GetMapping("/{id}")
    public RegistrationEntity getRegistrationById(@PathVariable Long id) {
        return registrationService.getRegistrationById(id);
    }

    // ✅ Update registration (can also be used to approve/reject)
    @PutMapping("/{id}")
    public RegistrationEntity updateRegistration(
            @PathVariable Long id,
            @RequestBody RegistrationModel registrationModel
    ) {
        return registrationService.updateRegistration(id, registrationModel);
    }

    // ✅ Delete registration
    @DeleteMapping("/{id}")
    public String deleteRegistration(@PathVariable Long id) {
        registrationService.deleteRegistration(id);
        return "Registration deleted successfully!";
    }

    // ✅ Filter + pagination + all counts
    @GetMapping("/filter")
    public RegistrationResponse getByFilters(
            @RequestParam(required = false) String by_status,
            @RequestParam(required = false) String by_name,
            @RequestParam(required = false) String by_eventtype,
            @RequestParam(required = false) String by_gender,
            @RequestParam(required = false) String by_registered_as,
            @RequestParam(required = false) String by_cnic,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return (RegistrationResponse) registrationService.getByFilters(
                by_status,
                by_name,
                by_eventtype,
                by_gender,
                by_registered_as,
                by_cnic,
                pageNumber,
                pageSize
        );
    }
}

