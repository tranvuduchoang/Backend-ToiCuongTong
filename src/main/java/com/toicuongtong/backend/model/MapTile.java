package com.toicuongtong.backend.model;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
 * Entity cho Map Tile (ô trong map)
 */
@Entity
@Table(name = "map_tiles", schema = "tct")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MapTileId.class)
public class MapTile {
    @Id
    @Column(name = "run_id")
    private Long runId;
    
    @Id
    @Column(name = "x")
    private Integer x;
    
    @Id
    @Column(name = "y")
    private Integer y;
    
    @Column(name = "tile_type", nullable = false)
    private String tileType;
    
    @Column(name = "visited", nullable = false)
    private Boolean visited = false;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reward", columnDefinition = "jsonb")
    private Map<String, Object> reward;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", insertable = false, updatable = false)
    private MapRun mapRun;
}

