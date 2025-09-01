package com.example.springservice.entites.enmap;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter


@Embeddable
public class ArtistStyleMappingId implements Serializable {
    private Integer userId;
    private Integer styleId;

    // equals + hashCode (จำเป็นสำหรับ JPA)
}
