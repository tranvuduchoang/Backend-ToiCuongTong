package com.toicuongtong.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity cho Map Spawn (quái vật xuất hiện trong map)
 */
@Entity
@Table(name = "map_spawn", schema = "tct")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MapSpawnId.class)
public class MapSpawn {
    @Id
    @Column(name = "map_id")
    private Long mapId;
    
    @Id
    @Column(name = "monster_id")
    private Long monsterId;
    
    @Column(name = "weight", nullable = false)
    private Integer weight = 1;
    
    @Column(name = "min_level", nullable = false)
    private Integer minLevel = 1;
    
    @Column(name = "max_level", nullable = false)
    private Integer maxLevel = 999;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id", insertable = false, updatable = false)
    private MapDef mapDef;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monster_id", insertable = false, updatable = false)
    private MonsterDef monsterDef;
}

