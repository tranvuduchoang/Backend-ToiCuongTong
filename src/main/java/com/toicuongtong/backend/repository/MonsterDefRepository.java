package com.toicuongtong.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.toicuongtong.backend.model.MonsterDef;

/**
 * Repository cho MonsterDef
 */
@Repository
public interface MonsterDefRepository extends JpaRepository<MonsterDef, Long> {
    
    Optional<MonsterDef> findByCode(String code);
    
    List<MonsterDef> findByDifficulty(MonsterDef.MapDifficulty difficulty);
    
    List<MonsterDef> findByRealmIdAndSublevel(Integer realmId, Integer sublevel);
    
    @Query("SELECT m FROM MonsterDef m WHERE m.difficulty = :difficulty AND m.realmId <= :realmId AND (m.sublevel IS NULL OR m.sublevel <= :sublevel)")
    List<MonsterDef> findMonstersByDifficultyAndLevel(@Param("difficulty") MonsterDef.MapDifficulty difficulty,
                                                      @Param("realmId") Integer realmId,
                                                      @Param("sublevel") Integer sublevel);
    
    @Query("SELECT m FROM MonsterDef m JOIN MapSpawn ms ON m.id = ms.monsterId WHERE ms.mapId = :mapId")
    List<MonsterDef> findMonstersByMapId(@Param("mapId") Long mapId);
}
