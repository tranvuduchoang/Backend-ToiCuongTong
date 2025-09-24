package com.toicuongtong.backend.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toicuongtong.backend.dto.CharacterCreationData;
import com.toicuongtong.backend.model.Avatar;
import com.toicuongtong.backend.model.CharacterCreationSession;
import com.toicuongtong.backend.model.Technique;
import com.toicuongtong.backend.model.User;
import com.toicuongtong.backend.service.GameDataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/game-data") // Tất cả API trong file này sẽ bắt đầu bằng /api/game-data
@RequiredArgsConstructor
public class GameDataController {

    private final GameDataService gameDataService;

    // API để lấy dữ liệu tạo nhân vật (10 avatars + 5 công pháp ngẫu nhiên)
    // Địa chỉ đầy đủ: GET http://localhost:8081/api/game-data/character-creation
    // Test endpoint để kiểm tra authentication
    @GetMapping("/test")
    public ResponseEntity<String> testAuth(@AuthenticationPrincipal User user) {
        System.out.println("GameDataController: Test endpoint called");
        System.out.println("GameDataController: User object: " + user);
        
        if (user == null) {
            System.out.println("GameDataController: User is null! Authentication failed.");
            return ResponseEntity.status(401).body("Authentication failed - User is null");
        }
        
        System.out.println("GameDataController: User ID: " + user.getId());
        System.out.println("GameDataController: User email: " + user.getEmail());
        
        return ResponseEntity.ok("Authentication successful! User: " + user.getEmail());
    }

    // Test endpoint để kiểm tra database data
    @GetMapping("/test-db")
    public ResponseEntity<String> testDatabase() {
        System.out.println("GameDataController: Testing database data...");
        
        try {
            // Test avatars
            List<Avatar> avatars = gameDataService.getRandomAvatars(10);
            System.out.println("GameDataController: Found " + avatars.size() + " avatars");
            
            // Test techniques
            List<Technique> techniques = gameDataService.getRandomTechniques(5);
            System.out.println("GameDataController: Found " + techniques.size() + " techniques");
            
            String result = "Database test successful! Avatars: " + avatars.size() + ", Techniques: " + techniques.size();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("GameDataController: Database test error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Database test failed: " + e.getMessage());
        }
    }

    // Test endpoint để kiểm tra session trực tiếp
    @GetMapping("/test-session")
    public ResponseEntity<String> testSession(@AuthenticationPrincipal User user) {
        System.out.println("GameDataController: Testing session for user: " + user.getEmail());
        
        try {
            Long userId = user.getId();
            System.out.println("GameDataController: User ID: " + userId);
            
            // Test tạo session
            CharacterCreationData data1 = gameDataService.getOrCreateCharacterCreationData(userId);
            System.out.println("GameDataController: First call - Avatar IDs: " + 
                data1.randomAvatars().stream().map(Avatar::getId).collect(Collectors.toList()));
            
            // Test tìm session
            CharacterCreationData data2 = gameDataService.getOrCreateCharacterCreationData(userId);
            System.out.println("GameDataController: Second call - Avatar IDs: " + 
                data2.randomAvatars().stream().map(Avatar::getId).collect(Collectors.toList()));
            
            boolean isConsistent = data1.randomAvatars().stream()
                .map(Avatar::getId)
                .collect(Collectors.toList())
                .equals(data2.randomAvatars().stream()
                    .map(Avatar::getId)
                    .collect(Collectors.toList()));
            
            String result = "Session test completed. Consistent: " + isConsistent;
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("GameDataController: Session test error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Session test failed: " + e.getMessage());
        }
    }

    // Test endpoint để kiểm tra database trực tiếp
    @GetMapping("/test-db-session")
    public ResponseEntity<String> testDatabaseSession(@AuthenticationPrincipal User user) {
        System.out.println("GameDataController: Testing database session for user: " + user.getEmail());
        
        try {
            Long userId = user.getId();
            System.out.println("GameDataController: User ID: " + userId);
            
            // Kiểm tra session trong database
            Optional<CharacterCreationSession> session = gameDataService.getSessionRepository().findById(userId);
            System.out.println("GameDataController: Session exists in DB: " + session.isPresent());
            
            if (session.isPresent()) {
                System.out.println("GameDataController: Session data: " + session.get().getSessionData());
            } else {
                System.out.println("GameDataController: No session found in database");
            }
            
            String result = "Database session test completed. Session exists: " + session.isPresent();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("GameDataController: Database session test error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Database session test failed: " + e.getMessage());
        }
    }

    @GetMapping("/character-creation")
    public ResponseEntity<CharacterCreationData> getCharacterCreationData(@AuthenticationPrincipal User user) {
        System.out.println("GameDataController: Received request for character creation data");
        System.out.println("GameDataController: User object: " + user);
        
        if (user == null) {
            System.out.println("GameDataController: User is null! Authentication failed.");
            return ResponseEntity.status(401).build();
        }
        
        System.out.println("GameDataController: User ID: " + user.getId());
        System.out.println("GameDataController: User email: " + user.getEmail());
        
        try {
            // Sử dụng userId trực tiếp thay vì playerId
            Long userId = user.getId();

            System.out.println("GameDataController: Calling GameDataService with userId: " + userId);
            CharacterCreationData data = gameDataService.getOrCreateCharacterCreationData(userId);
            System.out.println("GameDataController: Successfully got data from GameDataService");
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            System.err.println("GameDataController: Error in getCharacterCreationData: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}