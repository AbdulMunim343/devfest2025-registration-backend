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
            int pageNumber,
            int pageSize
    );

    RegistrationEntity getRegistrationById(Long id);
    RegistrationEntity updateRegistration(Long id, RegistrationModel registrationModel);
    RegistrationStatsModel getRegistrationStats();
    void deleteRegistration(Long id);
    void updateStatuses(List<Long> ids, String status);
    Map<String, Object> scanQRAndUpdateStatus(Long id, String status);
}
