package com.toicuongtong.backend.repository;

import com.toicuongtong.backend.model.PlayerTechnique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerTechniqueRepository extends JpaRepository<PlayerTechnique, Long> {
    // Interface này nên để trống như vậy là đủ các hàm cơ bản
}