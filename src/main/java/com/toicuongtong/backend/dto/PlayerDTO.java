package com.toicuongtong.backend.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {
    private Long id;
    private String name;
    private String avatarUrl;
    private boolean characterCreated; // Đổi tên để tránh vấn đề với Lombok
    private Map<String, Object> stats;
    private Long userId; // Thay vì trả về toàn bộ User object
}
