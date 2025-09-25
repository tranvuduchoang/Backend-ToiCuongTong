package com.toicuongtong.backend.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho thông tin Combat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombatDTO {
    private Long combatId;
    private Long playerId;
    private String playerName;
    private Long monsterId;
    private String monsterName;
    private String combatStatus; // "active", "victory", "defeat", "fled"
    private Integer currentTurn;
    private Integer maxTurns;
    private CombatParticipantDTO player;
    private CombatParticipantDTO monster;
    private List<CombatActionDTO> actionHistory;
    private String message;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CombatParticipantDTO {
        private Long id;
        private String name;
        private Integer currentHp;
        private Integer maxHp;
        private Integer currentMp;
        private Integer maxMp;
        private Map<String, Object> stats; // STR, AGI, DEF, ACC, CR, CD
        private List<CombatSkillDTO> availableSkills;
        private List<CombatEffectDTO> activeEffects;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CombatSkillDTO {
        private Long skillId;
        private String skillName;
        private String skillType;
        private String description;
        private Integer costMp;
        private Integer cdTurns;
        private Integer currentCooldown;
        private Boolean canUse;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CombatEffectDTO {
        private String effectType; // "buff", "debuff", "dot", "hot"
        private String effectName;
        private Integer duration;
        private Map<String, Object> effectData;
    }
}
