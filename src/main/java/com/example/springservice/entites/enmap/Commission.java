package com.example.springservice.entites.enmap;

import com.example.springservice.entites.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "commissions")
@Getter @Setter
public class Commission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String price;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private Status status = Status.REQUESTED;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Status {
        REQUESTED, ACCEPTED, REJECTED, IN_PROGRESS, DELIVERED, COMPLETED, CANCELLED
    }
}

