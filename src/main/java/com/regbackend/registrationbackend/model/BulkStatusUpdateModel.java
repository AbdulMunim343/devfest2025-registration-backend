package com.regbackend.registrationbackend.model;

import lombok.Data;

import java.util.List;

@Data
public class BulkStatusUpdateModel {
    private List<String> ids;     // IDs of registrations to update
    private String status;      // New status (e.g., APPROVED, REJECTED, PENDING)
}