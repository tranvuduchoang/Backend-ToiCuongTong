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
 * Entity cho Monster Drop (vật phẩm rơi từ quái vật)
 */
@Entity
@Table(name = "monster_drop", schema = "tct")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MonsterDropId.class)
public class MonsterDrop {
    @Id
    @Column(name = "monster_id")
    private Long monsterId;
    
    @Id
    @Column(name = "item_id")
    private Long itemId;
    
    @Column(name = "drop_chance", nullable = false, columnDefinition = "numeric")
    private Double dropChance;
    
    @Column(name = "min_qty", nullable = false)
    private Integer minQty = 1;
    
    @Column(name = "max_qty", nullable = false)
    private Integer maxQty = 1;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monster_id", insertable = false, updatable = false)
    private MonsterDef monsterDef;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private Item item;
}
