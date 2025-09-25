package com.toicuongtong.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.toicuongtong.backend.model.MapRun;

/**
 * Repository cho MapRun
 */
@Repository
public interface MapRunRepository extends JpaRepository<MapRun, Long> {
    
    List<MapRun> findByPlayerId(Long playerId);
    
    List<MapRun> findByPlayerIdAndMapId(Long playerId, Long mapId);
    
    @Query("SELECT mr FROM MapRun mr WHERE mr.playerId = :playerId AND mr.endedAt IS NULL ORDER BY mr.startedAt DESC")
    List<MapRun> findActiveRunsByPlayerId(@Param("playerId") Long playerId);
    
    @Query("SELECT mr FROM MapRun mr WHERE mr.playerId = :playerId AND mr.mapId = :mapId AND mr.endedAt IS NULL")
    Optional<MapRun> findActiveRunByPlayerAndMap(@Param("playerId") Long playerId, @Param("mapId") Long mapId);
    
    @Query("SELECT mr FROM MapRun mr WHERE mr.startedAt >= :startDate AND mr.startedAt <= :endDate")
    List<MapRun> findRunsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
