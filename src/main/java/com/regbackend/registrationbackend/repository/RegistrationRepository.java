package com.regbackend.registrationbackend.repository;

import com.regbackend.registrationbackend.entity.RegistrationEntity;
import com.regbackend.registrationbackend.enums.EventType;
import com.regbackend.registrationbackend.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationEntity, String> , JpaSpecificationExecutor<RegistrationEntity> {
    Optional<RegistrationEntity> findByPublicId(String publicId);
    long countByEventType(EventType eventType);
    long countByStatus(Status status);
    long countByRegisteredAs(String registeredAs);
    long countByGender(String gender);
    boolean existsByCnic(String cnic);
    boolean existsByEmail(String email);
}