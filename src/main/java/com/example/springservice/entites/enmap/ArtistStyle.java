package com.example.springservice.entites.enmap;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "Artist_Styles")
public class ArtistStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer styleId;

    @Column(nullable = false, length = 50)
    private String styleName;

}
