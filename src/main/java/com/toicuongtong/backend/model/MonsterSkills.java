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
 * Entity cho Monster Skills (liên kết quái vật với kỹ năng)
 */
@Entity
@Table(name = "monster_skills", schema = "tct")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MonsterSkillsId.class)
public class MonsterSkills {
    @Id
    @Column(name = "monster_id")
    private Long monsterId;
    
    @Id
    @Column(name = "skill_id")
    private Long skillId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monster_id", insertable = false, updatable = false)
    private MonsterDef monsterDef;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", insertable = false, updatable = false)
    private Skill skill;
}

