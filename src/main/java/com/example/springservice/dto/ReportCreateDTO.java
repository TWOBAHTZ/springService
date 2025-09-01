package com.example.springservice.dto;

// ✅ ReportCreateDTO.java
public class ReportCreateDTO {
    public Integer reportedUserId;
    public String reportType; // SCAM, HARASSMENT, LATE_DELIVERY
    public String description;
    public Integer commissionId; // optional
}