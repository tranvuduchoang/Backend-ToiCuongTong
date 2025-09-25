package com.toicuongtong.backend.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho hành động trong Combat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombatActionDTO {
    private Long actionId;
    private Long combatId;
    private String actionType; // "attack", "skill", "item", "flee"
    private String actorType; // "player", "monster"
    private Long actorId;
    private String actorName;
    private Long targetId;
    private String targetName;
    private Long skillId;
    private String skillName;
    private Integer damage;
    private Integer healing;
    private Integer mpCost;
    private String result; // "hit", "miss", "critical", "blocked", "dodged"
    private Map<String, Object> effects; // Các hiệu ứng phụ
    private String message;
    private Long timestamp;
}
