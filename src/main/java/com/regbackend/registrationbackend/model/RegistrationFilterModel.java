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
public class RegistrationFilterModel {
    private List<RegistrationEntity> registrations;
    private int pageNumber;
    private int totalPages;
    private long totalRegistrations;
}

