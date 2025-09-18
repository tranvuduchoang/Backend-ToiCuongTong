// File: src/main/java/com/toicuongtong/backend/repository/PlayerRepository.java
package com.toicuongtong.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.toicuongtong.backend.model.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByUserId(Long userId);
}