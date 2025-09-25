package com.toicuongtong.backend.model;

import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
 * Entity cho Monster Definition
 */
@Entity
@Table(name = "monster_def", schema = "tct")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonsterDef {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "code", unique = true, nullable = false)
    private String code;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private MapDifficulty difficulty;
    
    @Column(name = "realm_id")
    private Integer realmId;
    
    @Column(name = "sublevel")
    private Integer sublevel;
    
    @Column(name = "xp_reward")
    private Integer xpReward = 0;
    
    @Column(name = "gold_reward")
    private Integer goldReward = 0;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "stats", columnDefinition = "jsonb")
    private Map<String, Object> stats;
    
    @Column(name = "tags", columnDefinition = "text[]")
    private List<String> tags;
    
    @OneToMany(mappedBy = "monsterDef", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MonsterSkills> monsterSkills;
    
    @OneToMany(mappedBy = "monsterDef", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MonsterDrop> monsterDrops;
    
    public enum MapDifficulty {
        Pham_nhan, Tien_nhan, Thanh_nhan
    }
}
