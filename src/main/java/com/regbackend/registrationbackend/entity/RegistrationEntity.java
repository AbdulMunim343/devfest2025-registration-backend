package com.regbackend.registrationbackend.entity;

import com.regbackend.registrationbackend.enums.EventType;
import com.regbackend.registrationbackend.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===================== Basic Info =====================
    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true, length = 15)
    private String cnic;

    @Column(nullable = false)
    private String registeredAs; // e.g. "Student" or "Professional"

    private String jobRole; // only for professionals

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private String organizationOrUniversity;

    private String linkedInProfile;

    // ===================== Event Info =====================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    private String workshopName; // only if eventType == WORKSHOP

    // ===================== Status =====================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;
    private String gender;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ===================== Enums =====================



}

