package com.toicuongtong.backend.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Composite Key cho MonsterSkills
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonsterSkillsId implements Serializable {
    private Long monsterId;
    private Long skillId;
}
