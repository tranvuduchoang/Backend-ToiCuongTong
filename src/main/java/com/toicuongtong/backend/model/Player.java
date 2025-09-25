// File: src/main/java/com/toicuongtong/backend/model/Player.java
package com.toicuongtong.backend.model;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "players", schema = "tct")
@Data
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- MỐI QUAN HỆ ĐẶC BIỆT ---
    // @OneToOne: Đánh dấu mối quan hệ một-một: mỗi Player sẽ chỉ liên kết với duy
    // nhất một User.
    @OneToOne(fetch = FetchType.LAZY)
    // @JoinColumn: Chỉ rõ cột nào trong bảng "players" (cột user_id) được dùng để
    // tạo ra liên kết này, nó sẽ tham chiếu tới cột "id" của bảng users.
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private String name;

    // @JdbcTypeCode(SqlTypes.JSON): Một chỉ dẫn đặc biệt cho Spring biết rằng
    // trường 'stats' này nên được lưu trữ dưới dạng JSON trong database.
    // Điều này rất khớp với thiết kế ban đầu của bạn!
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb") // Chỉ định rõ kiểu dữ liệu trong PostgreSQL là jsonb
    private Map<String, Object> stats;

    // Các trường dữ liệu khác của Player sẽ được thêm vào đây sau,
    // ví dụ: realm_id, sublevel, exp, stamina, luck...
    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "character_created")
    private boolean isCharacterCreated = false;
    
    // Thêm các trường mới cho Combat System
    @Column(name = "current_realm_id")
    private Integer currentRealmId;
    
    @Column(name = "current_sublevel")
    private Integer currentSublevel;
    
    @Column(name = "experience")
    private Integer experience;
    
    @Column(name = "max_experience")
    private Integer maxExperience;
    
    @Column(name = "spirit_stones")
    private Integer spiritStones;
    
    @Column(name = "gold")
    private Integer gold;
    
    @Column(name = "reputation")
    private Integer reputation;
    
    @Column(name = "current_stamina")
    private Integer currentStamina;
    
    @Column(name = "max_stamina")
    private Integer maxStamina;
}