package com.toicuongtong.backend.repository;

import com.toicuongtong.backend.model.Technique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TechniqueRepository extends JpaRepository<Technique, Long> {

    /**
     * Lấy một số lượng công pháp ngẫu nhiên trực tiếp từ database.
     * @param count Số lượng cần lấy.
     * @return Danh sách các công pháp ngẫu nhiên.
     */
    // @Query: Cho phép chúng ta viết câu lệnh SQL của riêng mình.
    // nativeQuery = true: Báo rằng đây là câu lệnh SQL gốc của PostgreSQL.
    // ORDER BY RANDOM() LIMIT :count: Đây là cú pháp của PostgreSQL để
    // lấy ngẫu nhiên :count số dòng từ bảng.
    @Query(value = "SELECT * FROM tct.techniques ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    List<Technique> findRandomTechniques(@Param("count") int count);
}