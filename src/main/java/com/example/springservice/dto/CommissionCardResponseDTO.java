package com.example.springservice.dto;

import com.example.springservice.entites.CommissionCard;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CommissionCardResponseDTO {
    public Integer id;
    public String title;
    public String description;
    public String sampleImageUrl;
    public String price;
    public Integer estimatedDuration;
    public Boolean open;

    // 🔹 เพิ่ม artist info
    public Integer artistId;
    public String artistName;
    public String artistProfilePicture;

    public CommissionCardResponseDTO(CommissionCard card) {
        this.id = card.getId();
        this.title = card.getTitle();
        this.description = card.getDescription();
        this.sampleImageUrl = card.getSampleImageUrl();
        this.price = card.getPrice();
        this.estimatedDuration = card.getEstimatedDuration();
        this.open = card.getOpen();

        // ✅ inject artist info
        if (card.getArtist() != null) {
            this.artistId = card.getArtist().getUserId();
            this.artistName = card.getArtist().getName();
            this.artistProfilePicture = card.getArtist().getProfile_picture();
        }
    }
}



