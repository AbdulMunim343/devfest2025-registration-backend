package com.regbackend.registrationbackend.services;

import com.regbackend.registrationbackend.model.RegistrationFilterModel;
import com.regbackend.registrationbackend.model.RegistrationModel;
import com.regbackend.registrationbackend.entity.RegistrationEntity;
import com.regbackend.registrationbackend.model.RegistrationStatsModel;

import java.util.List;
import java.util.Map;

public interface RegistrationService {
    RegistrationEntity registerUser(RegistrationModel registrationModel);
    List<RegistrationEntity> getAllRegistrations();
    RegistrationFilterModel getByFilters(
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
    );

    RegistrationEntity getRegistrationById(String id);
    RegistrationEntity updateRegistration(String id, RegistrationModel registrationModel);
    RegistrationStatsModel getRegistrationStats();
    void deleteRegistration(String id);
    void updateStatuses(List<String> ids, String status);
    Map<String, Object> scanQRAndUpdateStatus(String id, String status);
}
