package com.toicuongtong.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toicuongtong.backend.dto.MapDTO;
import com.toicuongtong.backend.dto.MapExplorationDTO;
import com.toicuongtong.backend.service.MapService;

/**
 * Controller cho Map Exploration
 */
@RestController
@RequestMapping("/api/map")
@CrossOrigin(origins = "*")
public class MapController {
    
    @Autowired
    private MapService mapService;
    
    /**
     * Lấy danh sách maps có thể truy cập
     */
    @GetMapping("/available")
    public ResponseEntity<List<MapDTO>> getAvailableMaps(@RequestParam Long playerId) {
        try {
            List<MapDTO> maps = mapService.getAvailableMaps(playerId);
            return ResponseEntity.ok(maps);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Lấy thông tin chi tiết map
     */
    @GetMapping("/{mapId}")
    public ResponseEntity<MapDTO> getMapDetails(@PathVariable Long mapId) {
        try {
            MapDTO map = mapService.getMapDetails(mapId);
            if (map != null) {
                return ResponseEntity.ok(map);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Bắt đầu khám phá map
     */
    @PostMapping("/explore")
    public ResponseEntity<MapExplorationDTO> startMapExploration(
            @RequestParam Long playerId, 
            @RequestParam Long mapId) {
        try {
            MapExplorationDTO exploration = mapService.startMapExploration(playerId, mapId);
            return ResponseEntity.ok(exploration);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Di chuyển trong map
     */
    @PostMapping("/move")
    public ResponseEntity<MapExplorationDTO> moveInMap(
            @RequestParam Long playerId,
            @RequestParam Long runId,
            @RequestParam Integer x,
            @RequestParam Integer y) {
        try {
            MapExplorationDTO exploration = mapService.moveInMap(playerId, runId, x, y);
            return ResponseEntity.ok(exploration);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Kết thúc khám phá map
     */
    @PostMapping("/end")
    public ResponseEntity<String> endMapExploration(
            @RequestParam Long playerId,
            @RequestParam Long runId) {
        try {
            mapService.endMapExploration(playerId, runId);
            return ResponseEntity.ok("Map exploration ended successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
