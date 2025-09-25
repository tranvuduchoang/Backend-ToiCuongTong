package com.toicuongtong.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.toicuongtong.backend.model.MapSpawn;
import com.toicuongtong.backend.model.MapSpawnId;

/**
 * Repository cho MapSpawn
 */
@Repository
public interface MapSpawnRepository extends JpaRepository<MapSpawn, MapSpawnId> {
    
    List<MapSpawn> findByMapId(Long mapId);
    
    @Query("SELECT ms FROM MapSpawn ms WHERE ms.mapId = :mapId ORDER BY ms.weight DESC")
    List<MapSpawn> findByMapIdOrderByWeightDesc(@Param("mapId") Long mapId);
    
    @Query("SELECT ms FROM MapSpawn ms WHERE ms.mapId = :mapId AND ms.minLevel <= :level AND ms.maxLevel >= :level")
    List<MapSpawn> findByMapIdAndLevelRange(@Param("mapId") Long mapId, @Param("level") Integer level);
}
