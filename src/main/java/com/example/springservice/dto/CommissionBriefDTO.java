package com.example.springservice.dto;

import com.example.springservice.entites.CommissionBrief;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommissionBriefDTO {
    private Integer briefId;
    private Integer commissionId;
    private Integer customerId;
    private String fileUrl;
    private String fileType;
    private String description;
    private LocalDateTime uploadedAt;

    public static CommissionBriefDTO fromEntity(CommissionBrief brief) {
        CommissionBriefDTO dto = new CommissionBriefDTO();
        dto.setBriefId(brief.getBriefId());
        dto.setCommissionId(brief.getCommission().getCommissionId());
        dto.setCustomerId(brief.getCustomer().getUserId());
        dto.setFileUrl(brief.getFileUrl());
        dto.setFileType(brief.getFileType());
        dto.setDescription(brief.getDescription());
        dto.setUploadedAt(brief.getUploadedAt());
        return dto;
    }
}


