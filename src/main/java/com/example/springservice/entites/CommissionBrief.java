package com.example.springservice.entites;

import com.example.springservice.entites.enmap.Commission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "commission_briefs")
@Getter
@Setter
public class CommissionBrief {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer briefId;

    @ManyToOne
    @JoinColumn(name = "commission_id", nullable = false)
    private Commission commission;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    private String fileUrl;
    private String fileType;
    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime uploadedAt = LocalDateTime.now();
}
