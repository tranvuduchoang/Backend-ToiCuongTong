package com.toicuongtong.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho thông tin Map
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapDTO {
    private Long id;
    private String code;
    private String name;
    private Integer width;
    private Integer height;
    private String difficulty;
    private Integer requiredRealmId;
    private Integer requiredSublevel;
    private Integer staminaCost;
    private String description;
    private String recommendedRealmName;
    private List<MonsterSpawnDTO> monsterSpawns;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonsterSpawnDTO {
        private Long monsterId;
        private String monsterName;
        private Integer weight;
        private Integer minLevel;
        private Integer maxLevel;
    }
}
