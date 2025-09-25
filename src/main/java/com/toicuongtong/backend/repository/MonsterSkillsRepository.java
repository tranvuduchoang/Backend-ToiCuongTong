package com.toicuongtong.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.toicuongtong.backend.model.MonsterSkills;
import com.toicuongtong.backend.model.MonsterSkillsId;

/**
 * Repository cho MonsterSkills
 */
@Repository
public interface MonsterSkillsRepository extends JpaRepository<MonsterSkills, MonsterSkillsId> {
    
    List<MonsterSkills> findByMonsterId(Long monsterId);
    
    @Query("SELECT ms FROM MonsterSkills ms JOIN ms.skill s WHERE ms.monsterId = :monsterId")
    List<MonsterSkills> findByMonsterIdWithSkill(@Param("monsterId") Long monsterId);
    
    @Query("SELECT ms FROM MonsterSkills ms WHERE ms.monsterId = :monsterId AND ms.skillId = :skillId")
    Optional<MonsterSkills> findByMonsterIdAndSkillId(@Param("monsterId") Long monsterId, @Param("skillId") Long skillId);
}
