package com.example.springservice.entites.enmap;

import com.example.springservice.entites.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class CommissionFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Commission commission;

    @ManyToOne
    private User artist;

    private String fileUrl;
    private String fileType; // sketch / progress / final
    private String description;
    private String status = "active";
    private LocalDateTime uploadedAt = LocalDateTime.now();

    // Getter/Setter
}

