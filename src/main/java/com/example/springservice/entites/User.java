package com.example.springservice.entites;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter

@Entity
@Table(name = "Users")
public class User {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ArtistStyleMapping> styleMappings;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String role;

    @Column(columnDefinition = "TEXT")
    private String profile_picture;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('open','closed') DEFAULT 'open'")
    private CommissionStatus commission_status = CommissionStatus.open;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('active','banned','pending') DEFAULT 'active'")
    private Status status = Status.active;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updated_at = LocalDateTime.now();

    // Auto update timestamp
    @PreUpdate
    public void setUpdatedAt() {
        this.updated_at = LocalDateTime.now();
    }

    public String getProfilePicture() {
        return this.profile_picture != null ? this.profile_picture : "/images/default-avatar.png";
    }


    // --- ENUMS ---


    public enum CommissionStatus {
        open, closed
    }

    public enum Status {
        active, banned, pending
    }

    // --- GETTER/SETTER ---
    // Lombok (@Getter @Setter @Data)
}