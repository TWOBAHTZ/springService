package com.example.springservice.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter @Setter
@Entity
@Table(name = "commission_cards")
public class CommissionCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String price;

    @Column(name = "estimated_duration")
    private Integer estimatedDuration;

    @Column(name = "sample_image_url")
    private String sampleImageUrl;

    @Column(nullable = false)
    private Boolean open ;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deadline")
    private LocalDate deadline;

    public boolean isOpen() {
        return open;
    }


//    public boolean isOpen() {
//        return open;
//    }
}

