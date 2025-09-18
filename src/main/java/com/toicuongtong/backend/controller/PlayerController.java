package com.toicuongtong.backend.controller;

import com.toicuongtong.backend.dto.CreateCharacterRequest;
import com.toicuongtong.backend.model.Player;
import com.toicuongtong.backend.model.User;
import com.toicuongtong.backend.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    // API để kiểm tra xem nhân vật đã được tạo chưa
    // GET http://localhost:8081/api/player/status
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getPlayerStatus(@AuthenticationPrincipal User user) {
        // @AuthenticationPrincipal User user: Spring Security sẽ tự động lấy thông tin
        // của user đã đăng nhập từ JWT token và đưa vào đây.
        
        // Chúng ta cần tìm Player tương ứng với User này.
        // Giả sử User và Player có cùng ID hoặc có một liên kết trực tiếp.
        // Ta cần thêm hàm findByUser trong PlayerRepository.
        boolean isCreated = playerService.getCharacterCreationStatus(user.getId());
        return ResponseEntity.ok(Map.of("isCharacterCreated", isCreated));
    }

    // API để xử lý việc tạo nhân vật
    // POST http://localhost:8081/api/player/create-character
    @PostMapping("/create")
    public ResponseEntity<Player> createCharacter(
            @AuthenticationPrincipal User user,
            @RequestBody CreateCharacterRequest request) {
        
        Player updatedPlayer = playerService.createCharacter(user.getId(), request);
        return ResponseEntity.ok(updatedPlayer);
    }
}