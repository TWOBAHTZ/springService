package com.example.springservice.entites;

import com.example.springservice.entites.enmap.ArtistStyle;
import com.example.springservice.entites.enmap.ArtistStyleMappingId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "Artist_Style_Mapping")
public class ArtistStyleMapping {

    @EmbeddedId
    private ArtistStyleMappingId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("styleId")
    @JoinColumn(name = "style_id")
    private ArtistStyle style;

    // No @Id or @GeneratedValue here!
}

