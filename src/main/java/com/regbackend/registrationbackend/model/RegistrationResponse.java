package com.regbackend.registrationbackend.model;

import com.regbackend.registrationbackend.entity.RegistrationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationResponse {

    private List<RegistrationEntity> registrations;

    private int pageNumber;
    private int totalPages;
    private long totalRegistrations;

    private long totalWorkshopRegistrations;
    private long totalConferenceRegistrations;

    private long pendingCount;
    private long rejectedCount;
    private long approvedCount;
    private long attendedCount;

    private long professionalCount;
    private long studentCount;

    private long maleCount;
    private long femaleCount;
}

