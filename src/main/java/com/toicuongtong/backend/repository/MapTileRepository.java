package com.toicuongtong.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.toicuongtong.backend.model.MapTile;
import com.toicuongtong.backend.model.MapTileId;

/**
 * Repository cho MapTile
 */
@Repository
public interface MapTileRepository extends JpaRepository<MapTile, MapTileId> {
    
    List<MapTile> findByRunId(Long runId);
    
    List<MapTile> findByRunIdAndVisited(Long runId, Boolean visited);
    
    @Query("SELECT mt FROM MapTile mt WHERE mt.runId = :runId AND mt.x = :x AND mt.y = :y")
    Optional<MapTile> findByRunIdAndPosition(@Param("runId") Long runId, @Param("x") Integer x, @Param("y") Integer y);
    
    @Query("SELECT mt FROM MapTile mt WHERE mt.runId = :runId AND mt.tileType = :tileType")
    List<MapTile> findByRunIdAndTileType(@Param("runId") Long runId, @Param("tileType") String tileType);
    
    @Query("SELECT mt FROM MapTile mt WHERE mt.runId = :runId AND mt.visited = true")
    List<MapTile> findVisitedTilesByRunId(@Param("runId") Long runId);
    
    @Query("SELECT mt FROM MapTile mt WHERE mt.runId = :runId AND " +
           "mt.x >= :minX AND mt.x <= :maxX AND mt.y >= :minY AND mt.y <= :maxY")
    List<MapTile> findTilesInArea(@Param("runId") Long runId, 
                                  @Param("minX") Integer minX, @Param("maxX") Integer maxX,
                                  @Param("minY") Integer minY, @Param("maxY") Integer maxY);
}
