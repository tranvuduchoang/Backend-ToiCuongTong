package com.toicuongtong.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // **THÊM IMPORT QUAN TRỌNG NÀY**
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "techniques", schema = "tct")
@Data
@NoArgsConstructor
public class Technique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    // **ĐẢM BẢO CÓ TRƯỜNG NÀY**
    @Column
    private String description;

    @Column(name = "book_image_url")
    private String bookImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rarity rarity; // Đảm bảo có trường rarity

    @Column(columnDefinition = "text[]")
    private String[] tags; // Kiểu mảng String khớp với text[] trong DB

    // **THÊM ANNOTATION @JsonIgnore**
    @JsonIgnore
    @OneToMany(
        mappedBy = "technique",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<Skill> skills = new ArrayList<>();
}