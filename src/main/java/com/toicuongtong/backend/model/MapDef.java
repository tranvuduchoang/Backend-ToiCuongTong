package com.toicuongtong.backend.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity cho Map Definition
 */
@Entity
@Table(name = "map_def", schema = "tct")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapDef {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "code", unique = true, nullable = false)
    private String code;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "width", nullable = false)
    private Integer width = 50;
    
    @Column(name = "height", nullable = false)
    private Integer height = 50;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private MapDifficulty difficulty;
    
    @Column(name = "recommended_realm_id")
    private Integer recommendedRealmId;
    
    @Column(name = "required_realm_id")
    private Integer requiredRealmId;
    
    @Column(name = "required_sublevel")
    private Integer requiredSublevel;
    
    @Column(name = "stamina_cost")
    private Integer staminaCost = 5;
    
    @Column(name = "description")
    private String description;
    
    @OneToMany(mappedBy = "mapDef", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MapSpawn> monsterSpawns;
    
    public enum MapDifficulty {
        Pham_nhan, Tien_nhan, Thanh_nhan
    }
}
