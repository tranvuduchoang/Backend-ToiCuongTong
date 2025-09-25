package com.toicuongtong.backend.model;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity cho Item
 */
@Entity
@Table(name = "items", schema = "tct")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "code", unique = true, nullable = false)
    private String code;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ItemCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rarity", nullable = false)
    private Rarity rarity;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "world_grade")
    private WorldGrade worldGrade;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subgrade")
    private Subgrade subgrade;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "base_stats", columnDefinition = "jsonb")
    private Map<String, Object> baseStats;
    
    @Column(name = "stackable", nullable = false)
    private Boolean stackable = false;
    
    @Column(name = "is_unique", nullable = false)
    private Boolean isUnique = false;
    
    @Column(name = "description")
    private String description;
    
    public enum ItemCategory {
        weapon, armor, consumable, technique_book, crafting, quest, trinket
    }
    
    public enum WorldGrade {
        Thien, Dia, Huyen, Hoang
    }
    
    public enum Subgrade {
        so, trung, cao, tuyet
    }
}
