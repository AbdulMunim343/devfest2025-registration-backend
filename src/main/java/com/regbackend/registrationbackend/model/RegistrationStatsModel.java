package com.regbackend.registrationbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationStatsModel {
    private long totalRegistrations;
    private long totalWorkshopRegistrations;
    private long totalConferenceRegistrations;
    private long pendingRegistrations;
    private long rejectedRegistrations;
    private long shortlistedRegistrations;
    private long confirmedRegistrations;
    private long attendedRegistrations;
    private long professionalCount;
    private long studentCount;
    private long maleCount;
    private long femaleCount;
}
