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
    public RegistrationEntity registerUser(RegistrationModel model) {
        RegistrationEntity entity = new RegistrationEntity();

        // ✅ Map all form fields manually
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
        entity.setWorkshopName(model.getSelectedWorkshop()); // ✅ map selectedWorkshop → workshopName
        entity.setStatus(Status.PENDING); // default status
        entity.setCreatedAt(LocalDateTime.now());

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
