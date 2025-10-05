package com.regbackend.registrationbackend.model;

import com.regbackend.registrationbackend.enums.EventType;
import com.regbackend.registrationbackend.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationModel {

    private Long id;

    private String fullName;

    private String email;

    private String cnic;

    private String registeredAs; // Student or Professional

    private String jobRole; // only if registeredAs = Professional

    private String phoneNumber;

    private String organizationOrUniversity;

    private String linkedInProfile;

    private EventType eventType;

    private String workshopName; // only if eventType = WORKSHOP

    private Status status;

    private String createdAt;
}
