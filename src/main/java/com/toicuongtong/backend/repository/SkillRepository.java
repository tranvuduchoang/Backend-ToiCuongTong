package com.toicuongtong.backend.repository;

import com.toicuongtong.backend.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    // Spring Data JPA sẽ tự động cung cấp các hàm save(), findById(), findAll()...
    // Sau này nếu cần tìm skill theo tên, chúng ta có thể thêm hàm ở đây.
}