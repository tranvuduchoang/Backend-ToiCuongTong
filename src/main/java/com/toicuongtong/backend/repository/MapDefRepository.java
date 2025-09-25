package com.toicuongtong.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.toicuongtong.backend.model.MapDef;

/**
 * Repository cho MapDef
 */
@Repository
public interface MapDefRepository extends JpaRepository<MapDef, Long> {
    
    Optional<MapDef> findByCode(String code);
    
    List<MapDef> findByDifficulty(MapDef.MapDifficulty difficulty);
    
    List<MapDef> findByRequiredRealmIdAndRequiredSublevelLessThanEqual(Integer realmId, Integer sublevel);
    
    @Query("SELECT m FROM MapDef m WHERE m.requiredRealmId <= :realmId AND (m.requiredSublevel IS NULL OR m.requiredSublevel <= :sublevel)")
    List<MapDef> findAvailableMaps(@Param("realmId") Integer realmId, @Param("sublevel") Integer sublevel);
    
    @Query("SELECT m FROM MapDef m WHERE m.difficulty = :difficulty AND m.requiredRealmId <= :realmId AND (m.requiredSublevel IS NULL OR m.requiredSublevel <= :sublevel)")
    List<MapDef> findAvailableMapsByDifficulty(@Param("difficulty") MapDef.MapDifficulty difficulty, 
                                               @Param("realmId") Integer realmId, 
                                               @Param("sublevel") Integer sublevel);
}
