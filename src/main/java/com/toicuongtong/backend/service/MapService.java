package com.toicuongtong.backend.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toicuongtong.backend.dto.MapDTO;
import com.toicuongtong.backend.dto.MapExplorationDTO;
import com.toicuongtong.backend.model.MapDef;
import com.toicuongtong.backend.model.MapRun;
import com.toicuongtong.backend.model.MapSpawn;
import com.toicuongtong.backend.model.MapTile;
import com.toicuongtong.backend.model.MonsterDef;
import com.toicuongtong.backend.repository.MapDefRepository;
import com.toicuongtong.backend.repository.MapRunRepository;
import com.toicuongtong.backend.repository.MapSpawnRepository;
import com.toicuongtong.backend.repository.MapTileRepository;
import com.toicuongtong.backend.repository.MonsterDefRepository;

/**
 * Service cho Map Exploration
 */
@Service
@Transactional
public class MapService {
    
    @Autowired
    private MapDefRepository mapDefRepository;
    
    @Autowired
    private MapSpawnRepository mapSpawnRepository;
    
    @Autowired
    private MapRunRepository mapRunRepository;
    
    @Autowired
    private MapTileRepository mapTileRepository;
    
    @Autowired
    private MonsterDefRepository monsterDefRepository;
    
    // TODO: Implement monster skills and drops when needed
    // @Autowired
    // private MonsterSkillsRepository monsterSkillsRepository;
    
    // @Autowired
    // private MonsterDropRepository monsterDropRepository;
    
    @Autowired
    private PlayerService playerService;
    
    /**
     * Lấy danh sách maps có thể truy cập theo level player
     */
    public List<MapDTO> getAvailableMaps(Long playerId) {
        var player = playerService.getPlayerById(playerId);
        if (player == null) {
            return Collections.emptyList();
        }
        
        Integer realmId = Objects.requireNonNullElse(player.getCurrentRealmId(), 1);
        Integer sublevel = Objects.requireNonNullElse(player.getCurrentSublevel(), 1);
        
        List<MapDef> maps = mapDefRepository.findAvailableMaps(realmId, sublevel);
        return maps.stream().map(this::convertToMapDTO).collect(Collectors.toList());
    }
    
    /**
     * Lấy thông tin chi tiết map
     */
    public MapDTO getMapDetails(Long mapId) {
        Optional<MapDef> mapOpt = mapDefRepository.findById(mapId);
        if (mapOpt.isEmpty()) {
            return null;
        }
        
        return convertToMapDTO(mapOpt.get());
    }
    
    /**
     * Bắt đầu khám phá map
     */
    public MapExplorationDTO startMapExploration(Long playerId, Long mapId) {
        // Kiểm tra player có đủ stamina không
        var player = playerService.getPlayerById(playerId);
        if (player == null) {
            throw new RuntimeException("Player không tồn tại");
        }
        
        // Kiểm tra map có tồn tại không
        Optional<MapDef> mapOpt = mapDefRepository.findById(mapId);
        if (mapOpt.isEmpty()) {
            throw new RuntimeException("Map không tồn tại");
        }
        
        MapDef map = mapOpt.get();
        
        // Kiểm tra player có đang khám phá map khác không
        Optional<MapRun> activeRun = mapRunRepository.findActiveRunByPlayerAndMap(playerId, mapId);
        if (activeRun.isPresent()) {
            return convertToMapExplorationDTO(activeRun.get());
        }
        
        // Kiểm tra stamina
        if (player.getCurrentStamina() < map.getStaminaCost()) {
            throw new RuntimeException("Không đủ stamina để khám phá map này");
        }
        
        // Tạo map run mới
        MapRun mapRun = new MapRun();
        mapRun.setPlayerId(playerId);
        mapRun.setMapId(mapId);
        mapRun.setSeed(System.currentTimeMillis());
        mapRun.setStartedAt(LocalDateTime.now());
        
        mapRun = mapRunRepository.save(mapRun);
        
        // Trừ stamina
        player.setCurrentStamina(player.getCurrentStamina() - map.getStaminaCost());
        playerService.updatePlayer(player);
        
        // Tạo map tiles
        generateMapTiles(mapRun, map);
        
        return convertToMapExplorationDTO(mapRun);
    }
    
    /**
     * Di chuyển trong map
     */
    public MapExplorationDTO moveInMap(Long playerId, Long runId, Integer x, Integer y) {
        Optional<MapRun> runOpt = mapRunRepository.findById(runId);
        if (runOpt.isEmpty()) {
            throw new RuntimeException("Map run không tồn tại");
        }
        
        MapRun mapRun = runOpt.get();
        if (!mapRun.getPlayerId().equals(playerId)) {
            throw new RuntimeException("Không có quyền truy cập map run này");
        }
        
        if (mapRun.getEndedAt() != null) {
            throw new RuntimeException("Map run đã kết thúc");
        }
        
        // Kiểm tra vị trí hợp lệ
        if (x < 0 || x >= mapRun.getMapDef().getWidth() || 
            y < 0 || y >= mapRun.getMapDef().getHeight()) {
            throw new RuntimeException("Vị trí không hợp lệ");
        }
        
        // Lấy tile tại vị trí mới
        Optional<MapTile> tileOpt = mapTileRepository.findByRunIdAndPosition(runId, x, y);
        if (tileOpt.isEmpty()) {
            throw new RuntimeException("Tile không tồn tại");
        }
        
        MapTile tile = tileOpt.get();
        
        // Đánh dấu tile đã thăm
        if (!tile.getVisited()) {
            tile.setVisited(true);
            // MapTile sử dụng composite key, cần xử lý khác
            // TODO: Implement proper save logic for MapTile
        }
        
        // Xử lý tile type
        processTileType(tile, mapRun);
        
        return convertToMapExplorationDTO(mapRun);
    }
    
    /**
     * Kết thúc khám phá map
     */
    public void endMapExploration(Long playerId, Long runId) {
        Optional<MapRun> runOpt = mapRunRepository.findById(runId);
        if (runOpt.isEmpty()) {
            throw new RuntimeException("Map run không tồn tại");
        }
        
        MapRun mapRun = runOpt.get();
        if (!mapRun.getPlayerId().equals(playerId)) {
            throw new RuntimeException("Không có quyền truy cập map run này");
        }
        
        mapRun.setEndedAt(LocalDateTime.now());
        mapRunRepository.save(mapRun);
    }
    
    /**
     * Tạo map tiles cho map run
     */
    private void generateMapTiles(MapRun mapRun, MapDef map) {
        List<MapSpawn> spawns = mapSpawnRepository.findByMapIdOrderByWeightDesc(map.getId());
        
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                MapTile tile = new MapTile();
                tile.setRunId(mapRun.getId());
                tile.setX(x);
                tile.setY(y);
                tile.setVisited(false);
                
                // Xác định tile type dựa trên spawns
                String tileType = determineTileType(spawns, x, y, mapRun.getSeed());
                tile.setTileType(tileType);
                
                // Nếu là enemy tile, thêm thông tin monster
                if ("enemy".equals(tileType)) {
                    Map<String, Object> reward = new HashMap<>();
                    reward.put("monsterId", getRandomMonsterId(spawns, mapRun.getSeed() + x * 1000 + y));
                    tile.setReward(reward);
                }
                
                // MapTile sử dụng composite key, cần xử lý khác
                // TODO: Implement proper save logic for MapTile
            }
        }
    }
    
    /**
     * Xác định loại tile
     */
    private String determineTileType(List<MapSpawn> spawns, int x, int y, long seed) {
        // spawns parameter used for future monster spawn logic
        Random random = new Random(seed + x * 1000 + y);
        double roll = random.nextDouble();
        
        if (roll < 0.6) return "empty";
        if (roll < 0.8) return "enemy";
        if (roll < 0.9) return "treasure";
        if (roll < 0.95) return "resource";
        return "event";
    }
    
    /**
     * Lấy monster ID ngẫu nhiên
     */
    private Long getRandomMonsterId(List<MapSpawn> spawns, long seed) {
        if (spawns.isEmpty()) return null;
        
        Random random = new Random(seed);
        int totalWeight = spawns.stream().mapToInt(MapSpawn::getWeight).sum();
        int roll = random.nextInt(totalWeight);
        
        int currentWeight = 0;
        for (MapSpawn spawn : spawns) {
            currentWeight += spawn.getWeight();
            if (roll < currentWeight) {
                return spawn.getMonsterId();
            }
        }
        
        return spawns.get(0).getMonsterId();
    }
    
    /**
     * Xử lý loại tile
     */
    private void processTileType(MapTile tile, MapRun mapRun) {
        // Basic tile processing for UI testing
        switch (tile.getTileType()) {
            case "enemy":
                // Basic enemy encounter - just mark as processed
                System.out.println("Enemy encountered on tile: " + tile.getX() + "," + tile.getY());
                break;
            case "treasure":
                // Basic treasure collection - add some gold
                System.out.println("Treasure found on tile: " + tile.getX() + "," + tile.getY());
                break;
            case "resource":
                // Basic resource collection
                System.out.println("Resource found on tile: " + tile.getX() + "," + tile.getY());
                break;
            case "event":
                // Basic event processing
                System.out.println("Event triggered on tile: " + tile.getX() + "," + tile.getY());
                break;
            default:
                // Empty tile, no special action needed
                break;
        }
    }
    
    /**
     * Convert MapDef to MapDTO
     */
    private MapDTO convertToMapDTO(MapDef mapDef) {
        MapDTO dto = new MapDTO();
        dto.setId(mapDef.getId());
        dto.setCode(mapDef.getCode());
        dto.setName(mapDef.getName());
        dto.setWidth(mapDef.getWidth());
        dto.setHeight(mapDef.getHeight());
        dto.setDifficulty(mapDef.getDifficulty().name());
        dto.setRequiredRealmId(mapDef.getRequiredRealmId());
        dto.setRequiredSublevel(mapDef.getRequiredSublevel());
        dto.setStaminaCost(mapDef.getStaminaCost());
        dto.setDescription(mapDef.getDescription());
        
        // Lấy monster spawns
        List<MapSpawn> spawns = mapSpawnRepository.findByMapId(mapDef.getId());
        List<MapDTO.MonsterSpawnDTO> spawnDTOs = spawns.stream()
            .map(spawn -> {
                MapDTO.MonsterSpawnDTO spawnDTO = new MapDTO.MonsterSpawnDTO();
                spawnDTO.setMonsterId(spawn.getMonsterId());
                spawnDTO.setWeight(spawn.getWeight());
                spawnDTO.setMinLevel(spawn.getMinLevel());
                spawnDTO.setMaxLevel(spawn.getMaxLevel());
                
                // Lấy tên monster
                Optional<MonsterDef> monsterOpt = monsterDefRepository.findById(spawn.getMonsterId());
                if (monsterOpt.isPresent()) {
                    spawnDTO.setMonsterName(monsterOpt.get().getName());
                }
                
                return spawnDTO;
            })
            .collect(Collectors.toList());
        
        dto.setMonsterSpawns(spawnDTOs);
        
        return dto;
    }
    
    /**
     * Convert MapRun to MapExplorationDTO
     */
    private MapExplorationDTO convertToMapExplorationDTO(MapRun mapRun) {
        MapExplorationDTO dto = new MapExplorationDTO();
        dto.setRunId(mapRun.getId());
        dto.setPlayerId(mapRun.getPlayerId());
        dto.setMapId(mapRun.getMapId());
        dto.setMapName(mapRun.getMapDef().getName());
        dto.setMapWidth(mapRun.getMapDef().getWidth());
        dto.setMapHeight(mapRun.getMapDef().getHeight());
        dto.setSeed(mapRun.getSeed());
        
        // Lấy player info
        var player = playerService.getPlayerById(mapRun.getPlayerId());
        if (player != null) {
            dto.setCurrentStamina(player.getCurrentStamina());
            dto.setMaxStamina(player.getMaxStamina());
        }
        
        // Lấy tiles
        List<MapTile> tiles = mapTileRepository.findByRunId(mapRun.getId());
        List<MapExplorationDTO.MapTileDTO> tileDTOs = tiles.stream()
            .map(tile -> {
                MapExplorationDTO.MapTileDTO tileDTO = new MapExplorationDTO.MapTileDTO();
                tileDTO.setX(tile.getX());
                tileDTO.setY(tile.getY());
                tileDTO.setTileType(tile.getTileType());
                tileDTO.setVisited(tile.getVisited());
                tileDTO.setVisible(true); // Có thể thêm logic visibility
                tileDTO.setReward(tile.getReward());
                
                // Nếu là enemy tile, lấy thông tin monster
                if ("enemy".equals(tile.getTileType()) && tile.getReward() != null) {
                    Long monsterId = (Long) tile.getReward().get("monsterId");
                    if (monsterId != null) {
                        Optional<MonsterDef> monsterOpt = monsterDefRepository.findById(monsterId);
                        if (monsterOpt.isPresent()) {
                            tileDTO.setMonsterId(monsterId);
                            tileDTO.setMonsterName(monsterOpt.get().getName());
                            tileDTO.setMonsterLevel(monsterOpt.get().getSublevel());
                        }
                    }
                }
                
                return tileDTO;
            })
            .collect(Collectors.toList());
        
        dto.setVisibleTiles(tileDTOs);
        dto.setVisitedTiles(tileDTOs.stream().filter(MapExplorationDTO.MapTileDTO::getVisited).collect(Collectors.toList()));
        
        return dto;
    }
}
