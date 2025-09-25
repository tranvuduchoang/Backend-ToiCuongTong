package com.toicuongtong.backend.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho Map Exploration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapExplorationDTO {
    private Long runId;
    private Long playerId;
    private Long mapId;
    private String mapName;
    private Integer playerX;
    private Integer playerY;
    private Integer mapWidth;
    private Integer mapHeight;
    private Integer currentStamina;
    private Integer maxStamina;
    private String explorationStatus; // "exploring", "in_combat", "completed", "fled"
    private List<MapTileDTO> visibleTiles;
    private List<MapTileDTO> visitedTiles;
    private Long seed;
    private String message;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MapTileDTO {
        private Integer x;
        private Integer y;
        private String tileType; // "empty", "enemy", "treasure", "resource", "event"
        private Boolean visited;
        private Boolean visible;
        private Map<String, Object> reward;
        private Long monsterId;
        private String monsterName;
        private Integer monsterLevel;
    }
}
