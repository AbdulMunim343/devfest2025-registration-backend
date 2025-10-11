package com.regbackend.registrationbackend.services;

import com.regbackend.registrationbackend.entity.RegistrationEntity;
import com.regbackend.registrationbackend.enums.EventType;
import com.regbackend.registrationbackend.enums.Status;
import com.regbackend.registrationbackend.model.RegistrationModel;
import com.regbackend.registrationbackend.model.RegistrationStatsModel;
import com.regbackend.registrationbackend.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationServiceImp implements RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

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

        // ✅ Build and save new entity
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
                .reason(registrationModel.getReason())
                .createdAt(LocalDateTime.now())
                .build();

        return registrationRepository.save(entity);
    }

    @Override
    public List<RegistrationEntity> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    @Override
    public List<RegistrationEntity> getByFilters(String status, String name, String eventType, String gender, String registeredAs, String cnic, int pageNumber, int pageSize) {
        return List.of();
    }

    @Override
    public RegistrationEntity getRegistrationById(Long id) {
        return registrationRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Registration not found with ID: " + id));
    }

    @Override
    public RegistrationEntity updateRegistration(Long id, RegistrationModel model) {
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
        return null;
    }

    @Override
    public void deleteRegistration(Long id) {
        registrationRepository.deleteById(id);
    }
}
