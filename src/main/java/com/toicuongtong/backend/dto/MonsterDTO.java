package com.toicuongtong.backend.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho thông tin Monster
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonsterDTO {
    private Long id;
    private String code;
    private String name;
    private String difficulty;
    private Integer realmId;
    private Integer sublevel;
    private String realmName;
    private Integer xpReward;
    private Integer goldReward;
    private Map<String, Object> stats; // HP, ATK, DEF, AGI, ACC, CR, CD, RES_PSN, RES_BLD
    private List<String> tags;
    private List<MonsterSkillDTO> skills;
    private List<MonsterDropDTO> drops;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonsterSkillDTO {
        private Long skillId;
        private String skillName;
        private String skillType;
        private String description;
        private Map<String, Object> scaling;
        private Integer costMp;
        private Integer cdTurns;
        private List<String> tags;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonsterDropDTO {
        private Long itemId;
        private String itemName;
        private Double dropChance;
        private Integer minQty;
        private Integer maxQty;
    }
}
