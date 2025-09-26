package com.toicuongtong.backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toicuongtong.backend.dto.CreateCharacterRequest;
import com.toicuongtong.backend.dto.PlayerDTO;
import com.toicuongtong.backend.model.Player;
import com.toicuongtong.backend.model.User;
import com.toicuongtong.backend.service.PlayerService;

import lombok.RequiredArgsConstructor;

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

    // API để lấy thông tin chi tiết nhân vật
    // GET http://localhost:8081/api/player/data
    @GetMapping("/data")
    public ResponseEntity<PlayerDTO> getPlayerData(@AuthenticationPrincipal User user) {
        System.out.println("PlayerController: getPlayerData called for user ID: " + user.getId());
        try {
            PlayerDTO playerDTO = playerService.getPlayerData(user.getId());
            if (playerDTO == null) {
                System.out.println("PlayerController: No player found for user ID: " + user.getId());
                // Trả về 404 Not Found thay vì 403 Forbidden
                return ResponseEntity.notFound().build();
            }
            System.out.println("PlayerController: Player found: " + playerDTO.getName());
            return ResponseEntity.ok(playerDTO);
        } catch (Exception e) {
            System.out.println("PlayerController: Error getting player data: " + e.getMessage());
            // Log error for debugging
            throw e;
        }
    }

    // API để xử lý việc tạo nhân vật
    // POST http://localhost:8081/api/player/create-character
    @PostMapping("/create")
    public ResponseEntity<Player> createCharacter(
            @RequestBody CreateCharacterRequest request) {
        
        System.out.println("=== PlayerController.createCharacter ===");
        System.out.println("Request: " + request);
        
        // Lấy user từ SecurityContext thay vì @AuthenticationPrincipal
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + authentication);
        System.out.println("Authentication principal: " + authentication.getPrincipal());
        
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            System.out.println("No valid authentication found");
            return ResponseEntity.status(403).build();
        }
        
        User user = (User) authentication.getPrincipal();
        System.out.println("User ID: " + user.getId());
        System.out.println("User email: " + user.getEmail());
        
        try {
            Player updatedPlayer = playerService.createCharacter(user.getId(), request);
            System.out.println("Character created successfully: " + updatedPlayer.getId());
            return ResponseEntity.ok(updatedPlayer);
        } catch (Exception e) {
            System.out.println("Error creating character: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw để Spring Security xử lý
        }
    }

    // API để cập nhật rewards sau combat
    // POST http://localhost:8081/api/player/update-rewards
    @PostMapping("/update-rewards")
    public ResponseEntity<PlayerDTO> updateRewards(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> rewards) {
        
        System.out.println("=== PlayerController.updateRewards ===");
        System.out.println("User ID: " + user.getId());
        System.out.println("Rewards: " + rewards);
        
        PlayerDTO updatedPlayer = playerService.updateRewards(user.getId(), rewards);
        return ResponseEntity.ok(updatedPlayer);
    }

    // Test endpoint để debug authentication
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint(@AuthenticationPrincipal User user) {
        System.out.println("=== PlayerController.testEndpoint ===");
        System.out.println("User ID: " + user.getId());
        System.out.println("User email: " + user.getEmail());
        
        Map<String, Object> response = Map.of(
            "message", "Test endpoint working",
            "userId", user.getId(),
            "userEmail", user.getEmail()
        );
        return ResponseEntity.ok(response);
    }

    // Test endpoint với @RequestBody
    @PostMapping("/test-body")
    public ResponseEntity<Map<String, Object>> testBodyEndpoint(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> body) {
        System.out.println("=== PlayerController.testBodyEndpoint ===");
        System.out.println("User ID: " + user.getId());
        System.out.println("User email: " + user.getEmail());
        System.out.println("Request body: " + body);
        
        Map<String, Object> response = Map.of(
            "message", "Test body endpoint working",
            "userId", user.getId(),
            "userEmail", user.getEmail(),
            "receivedBody", body
        );
        return ResponseEntity.ok(response);
    }

    // Test endpoint với CreateCharacterRequest
    @PostMapping("/test-create-request")
    public ResponseEntity<Map<String, Object>> testCreateRequestEndpoint(
            @AuthenticationPrincipal User user,
            @RequestBody CreateCharacterRequest request) {
        System.out.println("=== PlayerController.testCreateRequestEndpoint ===");
        System.out.println("User ID: " + user.getId());
        System.out.println("User email: " + user.getEmail());
        System.out.println("Request: " + request);
        
        Map<String, Object> response = Map.of(
            "message", "Test create request endpoint working",
            "userId", user.getId(),
            "userEmail", user.getEmail(),
            "receivedRequest", Map.of(
                "avatarUrl", request.avatarUrl(),
                "techniqueIds", request.techniqueIds(),
                "stats", request.stats()
            )
        );
        return ResponseEntity.ok(response);
    }

    // Test endpoint để debug vấn đề 403
    @PostMapping("/debug-403")
    public ResponseEntity<Map<String, Object>> debug403Endpoint() {
        System.out.println("=== PlayerController.debug403Endpoint ===");
        
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + authentication);
        System.out.println("Authentication principal: " + (authentication != null ? authentication.getPrincipal() : "null"));
        
        if (authentication == null) {
            System.out.println("No authentication found");
            return ResponseEntity.status(403).body(Map.of("error", "No authentication"));
        }
        
        if (!(authentication.getPrincipal() instanceof User)) {
            System.out.println("Principal is not User: " + authentication.getPrincipal().getClass());
            return ResponseEntity.status(403).body(Map.of("error", "Principal is not User"));
        }
        
        User user = (User) authentication.getPrincipal();
        System.out.println("User found: " + user.getEmail());
        
        Map<String, Object> response = Map.of(
            "message", "Debug 403 endpoint working",
            "userId", user.getId(),
            "userEmail", user.getEmail(),
            "authenticationClass", authentication.getClass().getSimpleName(),
            "principalClass", authentication.getPrincipal().getClass().getSimpleName()
        );
        return ResponseEntity.ok(response);
    }
}