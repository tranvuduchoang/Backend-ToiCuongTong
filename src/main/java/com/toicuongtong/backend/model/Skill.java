// File: src/main/java/com/toicuongtong/backend/model/Skill.java
package com.toicuongtong.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "skills", schema = "tct")
@Data
@NoArgsConstructor
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- MỐI QUAN HỆ NGƯỢC LẠI ---
    // @ManyToOne: Nhiều Kỹ Năng (Many) thuộc về một Công Pháp (One).
    // @JoinColumn: Chỉ định cột "technique_id" trong bảng "skills" là khóa ngoại
    // liên kết tới bảng "techniques".
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technique_id", nullable = false)
    private Technique technique;

    @Column(nullable = false)
    private String name; // Tên chiêu thức, ví dụ: "Kiếm Quang"

    @Column(name = "skill_image_url")
    private String skillImageUrl; // Đường dẫn đến hình ảnh icon của kỹ năng

    @Enumerated(EnumType.STRING)
    private SkillType type; // Cần tạo Enum SkillType

    @Column
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> scaling;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "stat_bonuses", columnDefinition = "jsonb")
    private Map<String, Object> statBonuses;

    @Column(name = "cost_mp")
    private int costMp;

    @Column(name = "cd_turns")
    private int cdTurns;

    @Column(columnDefinition = "text[]")
    private String[] tags;

    // --- MỐI QUAN HỆ ĐIỀU KIỆN ---
    // Đây là một mối quan hệ phức tạp: Nhiều-Nhiều (ManyToMany) của một đối tượng với chính nó.
    // Một kỹ năng (ví dụ A3) có thể yêu cầu nhiều kỹ năng khác (A2, B1) để mở khóa.
    // Và một kỹ năng (ví dụ B1) cũng có thể là điều kiện cho nhiều kỹ năng khác (A3, C4...).
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "skill_prerequisites", schema = "tct", // Tên bảng trung gian để lưu mối quan hệ
        joinColumns = @JoinColumn(name = "skill_id"), // Cột trỏ đến skill cần mở khóa (A3)
        inverseJoinColumns = @JoinColumn(name = "required_skill_id") // Cột trỏ đến skill điều kiện (A2, B1)
    )
    @ToString.Exclude // Loại trừ khỏi hàm toString() để tránh vòng lặp vô hạn
    @EqualsAndHashCode.Exclude // Tương tự, loại trừ khỏi hàm equals và hashCode
    private Set<Skill> prerequisites = new HashSet<>();
}