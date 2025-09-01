package com.example.springservice.dto;

import lombok.*;

@Getter @Setter
@Data
public class CommissionBriefRequestDTO {
    public Integer cardId;

    public String fileUrl;
    public String fileType;

    public String description;
}



