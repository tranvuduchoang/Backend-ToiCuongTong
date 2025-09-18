package com.toicuongtong.backend.controller;

import com.toicuongtong.backend.dto.CharacterCreationData;
import com.toicuongtong.backend.model.Avatar;
import com.toicuongtong.backend.model.Technique;
import com.toicuongtong.backend.service.GameDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toicuongtong.backend.model.User; // Thêm import User
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Thêm import này

import java.util.List;

@RestController
@RequestMapping("/api/game-data") // Tất cả API trong file này sẽ bắt đầu bằng /api/game-data
@RequiredArgsConstructor
public class GameDataController {

    private final GameDataService gameDataService;

    // API để lấy danh sách avatar ngẫu nhiên
    // Địa chỉ đầy đủ: GET http://localhost:8081/api/game-data/avatars/random
    @GetMapping("/character-creation")
    public ResponseEntity<CharacterCreationData> getCharacterCreationData() {
        List<Avatar> avatars = gameDataService.getRandomAvatars(10);
        List<Technique> techniques = gameDataService.getRandomTechniques(5);
        CharacterCreationData data = new CharacterCreationData(avatars, techniques);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/character-creation")
    public ResponseEntity<CharacterCreationData> getCharacterCreationData(@AuthenticationPrincipal User user) {
        // Lấy playerId từ user đã đăng nhập
        // Lưu ý: Cần có hàm findByUserId trong PlayerRepository
        // Player player = playerRepository.findByUserId(user.getId()).orElseThrow();
        // Long playerId = player.getId();
        // Tạm thời, để đơn giản, giả sử user.id và player.id là một cho mục đích demo
        // này
        Long playerId = user.getId();

        CharacterCreationData data = gameDataService.getOrCreateCharacterCreationData(playerId);
        return ResponseEntity.ok(data);
    }
}