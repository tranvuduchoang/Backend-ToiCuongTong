package com.toicuongtong.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toicuongtong.backend.dto.CombatDTO;
import com.toicuongtong.backend.service.CombatService;

/**
 * Controller cho Combat System
 */
@RestController
@RequestMapping("/api/combat")
@CrossOrigin(origins = "*")
public class CombatController {
    
    @Autowired
    private CombatService combatService;
    
    /**
     * Bắt đầu trận chiến
     */
    @PostMapping("/start")
    public ResponseEntity<CombatDTO> startCombat(
            @RequestParam Long playerId,
            @RequestParam Long monsterId) {
        try {
            CombatDTO combat = combatService.startCombat(playerId, monsterId);
            return ResponseEntity.ok(combat);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Thực hiện hành động trong combat
     */
    @PostMapping("/action")
    public ResponseEntity<CombatDTO> performAction(
            @RequestParam Long combatId,
            @RequestParam String actionType,
            @RequestParam(required = false) Long skillId,
            @RequestParam(required = false) Long targetId) {
        try {
            CombatDTO combat = combatService.performAction(combatId, actionType, skillId, targetId);
            return ResponseEntity.ok(combat);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Thoát khỏi combat
     */
    @PostMapping("/flee")
    public ResponseEntity<CombatDTO> fleeCombat(@RequestParam Long combatId) {
        try {
            CombatDTO combat = combatService.fleeCombat(combatId);
            return ResponseEntity.ok(combat);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Lấy thông tin combat
     */
    @GetMapping("/{combatId}")
    public ResponseEntity<CombatDTO> getCombat(@PathVariable Long combatId) {
        try {
            CombatDTO combat = combatService.getCombat(combatId);
            if (combat != null) {
                return ResponseEntity.ok(combat);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
