package com.regbackend.registrationbackend.services;

import com.regbackend.registrationbackend.entity.RegistrationEntity;
import com.regbackend.registrationbackend.enums.EventType;
import com.regbackend.registrationbackend.enums.Status;
import com.regbackend.registrationbackend.model.RegistrationFilterModel;
import com.regbackend.registrationbackend.model.RegistrationModel;
import com.regbackend.registrationbackend.model.RegistrationStatsModel;
import com.regbackend.registrationbackend.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegistrationServiceImp implements RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public RegistrationEntity registerUser(RegistrationModel registrationModel) {

        // ✅ Check for duplicate CNIC
        if (registrationRepository.existsByCnic(registrationModel.getCnic())) {
            throw new IllegalArgumentException("CNIC already exists. Please use a different CNIC.");
        }

        // ✅ Check for duplicate Email
        if (registrationRepository.existsByEmail(registrationModel.getEmail())) {
            throw new IllegalArgumentException("Email already exists. Please use a different email.");
        }

        // ✅ Build new registration entity (UUID and publicId will be auto-generated)
        RegistrationEntity entity = RegistrationEntity.builder()
                .fullName(registrationModel.getFullName())
                .email(registrationModel.getEmail())
                .cnic(registrationModel.getCnic())
                .registeredAs(registrationModel.getRegisteredAs())
                .jobRole(registrationModel.getJobRole())
                .phoneNumber(registrationModel.getPhoneNumber())
                .organizationOrUniversity(registrationModel.getOrganizationOrUniversity())
                .linkedInProfile(registrationModel.getLinkedInProfile())
                .eventType(registrationModel.getEventType())
                .workshopName(registrationModel.getSelectedWorkshop())
                .status(Status.PENDING)
                .gender(registrationModel.getGender())
                .ambassador(registrationModel.getAmbassador())
                .allowDataUse(registrationModel.getAllowDataUse() != null ? registrationModel.getAllowDataUse() : false)  // default false if null
                .workshopExpectations(registrationModel.getWorkshopExpectations())
                .createdAt(LocalDateTime.now())
                .build();

        // ✅ Save entity (triggers @PrePersist to auto-generate UUID + publicId)
        RegistrationEntity savedEntity = registrationRepository.save(entity);

        // ✅ Return the saved entity with UUID + publicId
        return savedEntity;
    }


    @Override
    public List<RegistrationEntity> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    @Override
    public RegistrationFilterModel getByFilters(
            String status,
            String name,
            String eventType,
            String gender,
            String registeredAs,
            String cnic,
            String organizationOrUniversity,
            String phoneNumber,
            String ambassador,
            String email,
            String workshopName,
            int pageNumber,
            int pageSize
    ) {
        // 1. Safety Check: Ensure pageNumber is at least 1
        int validPage = (pageNumber < 1) ? 1 : pageNumber;

        // 2. CRITICAL FIX: Add Sorting
        // We sort by 'id' (or 'createdAt') to ensure the order is fixed.
        // If you don't sort, the DB might return the same rows for Page 1 and Page 2.
        Pageable pageable = PageRequest.of(validPage - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        Specification<RegistrationEntity> spec = Specification.where(null);

        // ... (Your existing specification logic remains exactly the same) ...
        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), Status.valueOf(status.toUpperCase())));
        }
        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("fullName")), "%" + name.toLowerCase() + "%"));
        }
        if (eventType != null && !eventType.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("eventType"), EventType.valueOf(eventType.toUpperCase())));
        }
        if (gender != null && !gender.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("gender")), gender.toLowerCase()));
        }
        if (registeredAs != null && !registeredAs.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("registeredAs")), registeredAs.toLowerCase()));
        }
        if (cnic != null && !cnic.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("cnic"), cnic));
        }
        if (organizationOrUniversity != null && !organizationOrUniversity.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("organizationOrUniversity"), organizationOrUniversity));
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("phoneNumber"), phoneNumber));
        }
        if (ambassador != null && !ambassador.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("ambassador"), ambassador));
        }
        if (email != null && !email.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("email"), email));
        }
        if (workshopName != null && !workshopName.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("workshopName"), workshopName));
        }

        // 3. Fetch Data
        Page<RegistrationEntity> pageResult = registrationRepository.findAll(spec, pageable);

        // 4. Map Response
        RegistrationFilterModel filterModel = new RegistrationFilterModel();
        filterModel.setRegistrations(pageResult.getContent());
        filterModel.setPageNumber(validPage); // Return the sanitized page number
        filterModel.setTotalPages(pageResult.getTotalPages());
        filterModel.setTotalRegistrations(pageResult.getTotalElements());

        return filterModel;
    }


    @Override
    public RegistrationEntity getRegistrationById(String id) {
        return registrationRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Registration not found with ID: " + id));
    }

    @Override
    public RegistrationEntity updateRegistration(String id, RegistrationModel model) {
        RegistrationEntity entity = getRegistrationById(id);

        entity.setFullName(model.getFullName());
        entity.setEmail(model.getEmail());
        entity.setCnic(model.getCnic());
        entity.setRegisteredAs(model.getRegisteredAs());
        entity.setJobRole(model.getJobRole());
        entity.setPhoneNumber(model.getPhoneNumber());
        entity.setOrganizationOrUniversity(model.getOrganizationOrUniversity());
        entity.setLinkedInProfile(model.getLinkedInProfile());
        entity.setGender(model.getGender());
        entity.setEventType(model.getEventType());
        entity.setWorkshopName(model.getSelectedWorkshop());

        return registrationRepository.save(entity);
    }

    @Override
    public RegistrationStatsModel getRegistrationStats() {
        RegistrationStatsModel stats = new RegistrationStatsModel();

        stats.setTotalRegistrations(registrationRepository.count());
        stats.setTotalWorkshopRegistrations(registrationRepository.countByEventType(EventType.WORKSHOP));
        stats.setTotalConferenceRegistrations(registrationRepository.countByEventType(EventType.CONFERENCE));

        stats.setPendingRegistrations(registrationRepository.countByStatus(Status.PENDING));
        stats.setRejectedRegistrations(registrationRepository.countByStatus(Status.REJECTED));
        stats.setShortlistedRegistrations(registrationRepository.countByStatus(Status.SHORTLISTED));
        stats.setAttendedRegistrations(registrationRepository.countByStatus(Status.ATTENDED));
        stats.setConfirmedRegistrations(registrationRepository.countByStatus(Status.CONFIRMED));

        stats.setProfessionalCount(registrationRepository.countByRegisteredAs("professional"));
        stats.setStudentCount(registrationRepository.countByRegisteredAs("student"));

        stats.setMaleCount(registrationRepository.countByGender("male"));
        stats.setFemaleCount(registrationRepository.countByGender("female"));

        return stats;
    }

    @Override
    public void deleteRegistration(String id) {
        registrationRepository.deleteById(id);
    }

    public void updateStatuses(List<String> ids, String status) {

        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("At least one ID must be provided");
        }

        Status newStatus;
        try {
            newStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        List<RegistrationEntity> registrations = registrationRepository.findAllById(ids);

        for (RegistrationEntity reg : registrations) {

            Status previousStatus = reg.getStatus();
            reg.setStatus(newStatus);

            if (newStatus == Status.SHORTLISTED && previousStatus != Status.SHORTLISTED) {

                // Ensure publicId is NOT null
                if (reg.getPublicId() == null || reg.getPublicId().isEmpty()) {
                    throw new RuntimeException("Public ID is missing for user: " + reg.getEmail());
                }

                // Send email only ONCE
                emailService.sendApprovalEmail(
                        reg.getEmail(),
                        reg.getFullName(),
                        reg.getCnic(),
                        reg.getEventType().toString(),
                        reg.getPublicId(),
                        reg.getWorkshopName()
                );
            }
        }

        registrationRepository.saveAll(registrations);
    }



    @Override
    public Map<String, Object> scanQRAndUpdateStatus(String publicId, String status) {
        RegistrationEntity registration = registrationRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("User not found with Public ID: " + publicId));

        Map<String, Object> response = new HashMap<>();
        response.put("name", registration.getFullName());
        response.put("cnic", registration.getCnic());
        response.put("eventType", registration.getEventType());
        if(registration.getEventType() == EventType.WORKSHOP) {
            response.put("workshopName", registration.getWorkshopName());
        }

        // ✅ Convert and validate status
        Status newStatus;
        try {
            newStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }

        // ✅ Check if already updated
        if (registration.getStatus() == newStatus) {
            response.put("status", registration.getStatus());
            response.put("message", "This person is already marked as " + newStatus);
            return response;
        }

        // ✅ Update and save
        registration.setStatus(newStatus);
        registrationRepository.save(registration);

        response.put("status", registration.getStatus());
        response.put("message", "Status updated successfully");

        return response;
    }





}
