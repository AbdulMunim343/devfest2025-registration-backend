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
public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long>, JpaSpecificationExecutor<RegistrationEntity> {

    Optional<RegistrationEntity> findByEmail(String email);

    Optional<RegistrationEntity> findByCnic(String cnic);

    List<RegistrationEntity> findByStatus(Status status);

    List<RegistrationEntity> findByEventType(EventType eventType);

    List<RegistrationEntity> findByRegisteredAs(String registeredAs);

    List<RegistrationEntity> findByFullNameContainingIgnoreCase(String fullName);

    long countByEventType(EventType eventType);

    long countByStatus(Status status);

    long countByRegisteredAs(String registeredAs);

    long countByGender(String gender);

    boolean existsByCnic(String cnic);

    boolean existsByEmail(String email);
}