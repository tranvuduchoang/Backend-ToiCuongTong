package com.toicuongtong.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toicuongtong.backend.model.Player;
import com.toicuongtong.backend.model.PlayerTechnique;

@Repository
public interface PlayerTechniqueRepository extends JpaRepository<PlayerTechnique, Long> {
    // Xóa tất cả PlayerTechnique theo player
    void deleteAllByPlayer(Player player);
}