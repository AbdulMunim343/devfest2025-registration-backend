package com.regbackend.registrationbackend.services;

import com.regbackend.registrationbackend.entity.RegistrationEntity;
import com.regbackend.registrationbackend.enums.EventType;
import com.regbackend.registrationbackend.enums.Status;
import com.regbackend.registrationbackend.model.RegistrationModel;
import com.regbackend.registrationbackend.model.RegistrationResponse;
import com.regbackend.registrationbackend.repository.RegistrationRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RegistrationServiceImp implements RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Override
    public RegistrationEntity registerUser(RegistrationModel registrationModel) {
        RegistrationEntity entity = new RegistrationEntity();
        entity.setFullName(registrationModel.getFullName());
        entity.setEmail(registrationModel.getEmail());
        entity.setCnic(registrationModel.getCnic());
        entity.setRegisteredAs(registrationModel.getRegisteredAs());
        entity.setPhoneNumber(registrationModel.getPhoneNumber());
        entity.setOrganizationOrUniversity(registrationModel.getOrganizationOrUniversity());
        entity.setJobRole(registrationModel.getJobRole());
        entity.setLinkedInProfile(registrationModel.getLinkedInProfile());
        entity.setEventType(registrationModel.getEventType());
        entity.setWorkshopName(registrationModel.getWorkshopName());
        entity.setStatus(Status.PENDING);
        return registrationRepository.save(entity);
    }

    @Override
    public List<RegistrationEntity> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    @Override
    public RegistrationEntity getRegistrationById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found with id: " + id));
    }

    @Override
    public RegistrationEntity updateRegistration(Long id, RegistrationModel registrationModel) {
        RegistrationEntity entity = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found with id: " + id));

        entity.setFullName(registrationModel.getFullName());
        entity.setEmail(registrationModel.getEmail());
        entity.setCnic(registrationModel.getCnic());
        entity.setRegisteredAs(registrationModel.getRegisteredAs());
        entity.setPhoneNumber(registrationModel.getPhoneNumber());
        entity.setOrganizationOrUniversity(registrationModel.getOrganizationOrUniversity());
        entity.setJobRole(registrationModel.getJobRole());
        entity.setLinkedInProfile(registrationModel.getLinkedInProfile());
        entity.setEventType(registrationModel.getEventType());
        entity.setWorkshopName(registrationModel.getWorkshopName());
        entity.setStatus(registrationModel.getStatus() != null ? registrationModel.getStatus() : entity.getStatus());

        return registrationRepository.save(entity);
    }

    @Override
    public void deleteRegistration(Long id) {
        registrationRepository.deleteById(id);
    }

    @Override
    public List<RegistrationEntity> getByFilters(
            String status,
            String name,
            String eventType,
            String gender,
            String registeredAs,
            String cnic,
            int pageNumber,
            int pageSize
    ) {
        Specification<RegistrationEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), Status.valueOf(status.toUpperCase())));
            }
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("fullName")), "%" + name.toLowerCase() + "%"));
            }
            if (eventType != null && !eventType.isEmpty()) {
                predicates.add(cb.equal(root.get("eventType"), EventType.valueOf(eventType.toUpperCase())));
            }
            if (gender != null && !gender.isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("gender")), gender.toLowerCase()));
            }
            if (registeredAs != null && !registeredAs.isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("registeredAs")), registeredAs.toLowerCase()));
            }
            if (cnic != null && !cnic.isEmpty()) {
                predicates.add(cb.equal(root.get("cnic"), cnic));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
        Page<RegistrationEntity> page = registrationRepository.findAll(spec, pageable);

        List<RegistrationEntity> allRegistrations = registrationRepository.findAll();

        // 🔢 Calculate all counts
        long totalRegistrations = allRegistrations.size();
        long totalWorkshop = allRegistrations.stream().filter(r -> r.getEventType() == EventType.WORKSHOP).count();
        long totalConference = allRegistrations.stream().filter(r -> r.getEventType() == EventType.CONFERENCE).count();

        long pending = allRegistrations.stream().filter(r -> r.getStatus() == Status.PENDING).count();
        long approved = allRegistrations.stream().filter(r -> r.getStatus() == Status.APPROVED).count();
        long rejected = allRegistrations.stream().filter(r -> r.getStatus() == Status.REJECTED).count();
        long attended = allRegistrations.stream().filter(r -> r.getStatus() == Status.ATTENDED).count();

        long professional = allRegistrations.stream()
                .filter(r -> r.getRegisteredAs() != null && r.getRegisteredAs().equalsIgnoreCase("professional"))
                .count();
        long student = allRegistrations.stream()
                .filter(r -> r.getRegisteredAs() != null && r.getRegisteredAs().equalsIgnoreCase("student"))
                .count();

        long male = allRegistrations.stream()
                .filter(r -> r.getGender() != null && r.getGender().equalsIgnoreCase("male"))
                .count();
        long female = allRegistrations.stream()
                .filter(r -> r.getGender() != null && r.getGender().equalsIgnoreCase("female"))
                .count();

        // 🧾 Build Response
        return RegistrationResponse.builder()
                .registrations(page.getContent())
                .pageNumber(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalRegistrations(totalRegistrations)
                .totalWorkshopRegistrations(totalWorkshop)
                .totalConferenceRegistrations(totalConference)
                .pendingCount(pending)
                .approvedCount(approved)
                .rejectedCount(rejected)
                .attendedCount(attended)
                .professionalCount(professional)
                .studentCount(student)
                .maleCount(male)
                .femaleCount(female)
                .build().getRegistrations();
    }
}

