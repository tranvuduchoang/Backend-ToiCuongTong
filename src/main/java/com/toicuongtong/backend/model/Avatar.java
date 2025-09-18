package com.toicuongtong.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "avatars", schema = "tct")
@Data
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", unique = true, nullable = false)
    private String imageUrl;

    @Column
    private String rarity;
}