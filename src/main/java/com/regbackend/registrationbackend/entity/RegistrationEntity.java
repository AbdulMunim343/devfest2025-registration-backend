package com.regbackend.registrationbackend.entity;

import com.regbackend.registrationbackend.enums.EventType;
import com.regbackend.registrationbackend.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationEntity {

    // ===================== Primary ID (UUID) =====================
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, unique = true, length = 36)
    private String id = UUID.randomUUID().toString(); // example: "bda183f0-bd0a-4a78-bd79-0ecbf7c4e6ff"

    // ===================== Public ID =====================
    @Column(unique = true, updatable = false)
    private String publicId; // example: "REG-00123"

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
    private String ambassador;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ===================== New Fields =====================
    @Column(nullable = true)
    private Boolean allowDataUse = false;  // default false

    @Column(nullable = true)
    private String workshopExpectations;

    // ===================== Helper Methods =====================
    @PrePersist
    public void generatePublicId() {
        if (this.publicId == null) {
            // Auto-generate REG-XXXXX format using the last 5 chars of UUID or a sequence number logic
            String uniquePart = String.valueOf(Math.abs(UUID.randomUUID().toString().hashCode() % 100000));
            this.publicId = String.format("REG-%05d", Integer.parseInt(uniquePart));
        }
    }
}
