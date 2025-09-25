package com.toicuongtong.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity cho Map Run (lần khám phá map)
 */
@Entity
@Table(name = "map_runs", schema = "tct")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "player_id", nullable = false)
    private Long playerId;
    
    @Column(name = "map_id", nullable = false)
    private Long mapId;
    
    @Column(name = "seed", nullable = false)
    private Long seed;
    
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;
    
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", insertable = false, updatable = false)
    private Player player;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id", insertable = false, updatable = false)
    private MapDef mapDef;
    
    @OneToMany(mappedBy = "mapRun", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MapTile> mapTiles;
}
