package com.example.springservice.dto;

import com.example.springservice.entites.Report;

public class ReportAdminDTO {
    public Integer reportId;
    public String reportType;
    public String description;
    public String status;
    public String createdAt;

    public Integer reporterId;
    public String reporterName;

    public Integer reportedUserId;
    public String reportedUserName;

    public Integer commissionId;

    public Integer resolvedByAdminId;
    public String resolvedByName;
    public String resolvedAt;

    public ReportAdminDTO(Report report) {
        this.reportId = report.getReportId();
        this.reportType = report.getReportType().name();
        this.description = report.getDescription();
        this.status = report.getStatus().name();
        this.createdAt = report.getCreatedAt().toString();

        if (report.getReporter() != null) {
            this.reporterId = report.getReporter().getUserId();
            this.reporterName = report.getReporter().getName();
        }

        if (report.getReportedUser() != null) {
            this.reportedUserId = report.getReportedUser().getUserId();
            this.reportedUserName = report.getReportedUser().getName();
        }

        if (report.getCommission() != null) {
            this.commissionId = report.getCommission().getCommissionId();
        }

        if (report.getResolvedBy() != null) {
            this.resolvedByAdminId = report.getResolvedBy().getUserId();
            this.resolvedByName = report.getResolvedBy().getName();
        }

        if (report.getResolvedAt() != null) {
            this.resolvedAt = report.getResolvedAt().toString();
        }
    }
}
