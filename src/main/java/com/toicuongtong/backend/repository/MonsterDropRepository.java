package com.toicuongtong.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.toicuongtong.backend.model.MonsterDrop;
import com.toicuongtong.backend.model.MonsterDropId;

/**
 * Repository cho MonsterDrop
 */
@Repository
public interface MonsterDropRepository extends JpaRepository<MonsterDrop, MonsterDropId> {
    
    List<MonsterDrop> findByMonsterId(Long monsterId);
    
    @Query("SELECT md FROM MonsterDrop md JOIN md.item i WHERE md.monsterId = :monsterId")
    List<MonsterDrop> findByMonsterIdWithItem(@Param("monsterId") Long monsterId);
    
    @Query("SELECT md FROM MonsterDrop md WHERE md.monsterId = :monsterId AND md.dropChance >= :minChance")
    List<MonsterDrop> findByMonsterIdAndMinChance(@Param("monsterId") Long monsterId, @Param("minChance") Double minChance);
}
