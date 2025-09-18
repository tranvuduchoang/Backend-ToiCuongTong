// File: model/PlayerTechnique.java

package com.toicuongtong.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "player_techniques", schema = "tct")
@Data
public class PlayerTechnique {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne @JoinColumn(name = "technique_id")
    private Technique technique;

    private int mastery = 0;
}