package com.example.springservice.dto;

import com.example.springservice.entites.enmap.Commission;

public class CommissionResponseDTO {
    public Integer id;
    public String title;
    public String description;
    public String price;
    public String status;
    public String deadline;
    public UserSummaryDTO customer;
    public UserSummaryDTO artist;

    public CommissionResponseDTO(Commission c) {
        this.id = c.getCommissionId();
        this.title = c.getTitle();
        this.description = c.getDescription();
        this.price = c.getPrice();
        this.status = c.getStatus().name();
        this.deadline = c.getDeadline() != null ? c.getDeadline().toString() : null;
        this.customer = new UserSummaryDTO(c.getCustomer());
        this.artist = new UserSummaryDTO(c.getArtist());
    }
}

