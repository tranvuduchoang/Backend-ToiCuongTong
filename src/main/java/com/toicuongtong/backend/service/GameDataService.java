package com.toicuongtong.backend.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.toicuongtong.backend.dto.CharacterCreationData;
import com.toicuongtong.backend.model.Avatar;
import com.toicuongtong.backend.model.CharacterCreationSession;
import com.toicuongtong.backend.model.Technique;
import com.toicuongtong.backend.repository.AvatarRepository;
import com.toicuongtong.backend.repository.CharacterCreationSessionRepository;
import com.toicuongtong.backend.repository.TechniqueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameDataService {

    private final AvatarRepository avatarRepository;
    private final TechniqueRepository techniqueRepository;
    private final CharacterCreationSessionRepository sessionRepository;
    
    public CharacterCreationData getOrCreateCharacterCreationData(Long userId) {
        System.out.println("GameDataService: Getting character creation data for user " + userId);
        
        try {
            System.out.println("GameDataService: Searching for existing session with userId: " + userId);
            Optional<CharacterCreationSession> existingSession = sessionRepository.findById(userId);
            System.out.println("GameDataService: Session found: " + existingSession.isPresent());

            if (existingSession.isPresent()) {
                System.out.println("GameDataService: Found existing session for user " + userId);
                // --- TRƯỜNG HỢP 1: Người chơi đã có phiên, tải lại dữ liệu cũ ---
                String sessionData = existingSession.get().getSessionData();
                System.out.println("GameDataService: Session data: " + sessionData);
                
                String[] parts = sessionData.split(";");
                String avatarIdsStr = parts[0].substring("avatarIds:".length());
                String techniqueIdsStr = parts[1].substring("techniqueIds:".length());
                
                System.out.println("GameDataService: Avatar IDs from session: " + avatarIdsStr);
                System.out.println("GameDataService: Technique IDs from session: " + techniqueIdsStr);
                
                List<Long> avatarIds = Arrays.stream(avatarIdsStr.split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
                List<Long> techniqueIds = Arrays.stream(techniqueIdsStr.split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());

                List<Avatar> avatars = avatarRepository.findAllById(avatarIds);
                List<Technique> techniques = techniqueRepository.findAllById(techniqueIds);
                
                System.out.println("GameDataService: Loaded " + avatars.size() + " avatars and " + techniques.size() + " techniques from session");
                return new CharacterCreationData(avatars, techniques);

            } else {
                System.out.println("GameDataService: Creating new session for user " + userId);
                // --- TRƯỜNG HỢP 2: Người chơi mới, tạo phiên mới ---
                List<Avatar> randomAvatars = getRandomAvatars(10);
                List<Technique> randomTechniques = getRandomTechniques(5);

                System.out.println("GameDataService: Got " + randomAvatars.size() + " random avatars and " + randomTechniques.size() + " random techniques");

                // Lưu ID của các lựa chọn này vào session
                List<Long> avatarIds = randomAvatars.stream().map(Avatar::getId).collect(Collectors.toList());
                List<Long> techniqueIds = randomTechniques.stream().map(Technique::getId).collect(Collectors.toList());
                
                System.out.println("GameDataService: Avatar IDs: " + avatarIds);
                System.out.println("GameDataService: Technique IDs: " + techniqueIds);
                
                // Sử dụng String thay vì List để tránh vấn đề JSON serialization
                String avatarIdsStr = avatarIds.stream().map(String::valueOf).collect(Collectors.joining(","));
                String techniqueIdsStr = techniqueIds.stream().map(String::valueOf).collect(Collectors.joining(","));
                
                String sessionData = "avatarIds:" + avatarIdsStr + ";techniqueIds:" + techniqueIdsStr;
                
                System.out.println("GameDataService: Session data created: " + sessionData);
                
                try {
                    CharacterCreationSession newSession = new CharacterCreationSession(userId, sessionData);
                    System.out.println("GameDataService: CharacterCreationSession created successfully");
                    
                    CharacterCreationSession savedSession = sessionRepository.save(newSession);
                    System.out.println("GameDataService: Saved new session for user " + userId + " with ID: " + savedSession.getUserId());
                    
                    // Test ngay sau khi lưu
                    Optional<CharacterCreationSession> testSession = sessionRepository.findById(userId);
                    System.out.println("GameDataService: Test - Session exists after save: " + testSession.isPresent());
                    if (testSession.isPresent()) {
                        System.out.println("GameDataService: Test - Session data: " + testSession.get().getSessionData());
                    }
                } catch (Exception e) {
                    System.err.println("GameDataService: Error saving session: " + e.getMessage());
                    e.printStackTrace();
                    // Tiếp tục mà không lưu session
                }
                
                System.out.println("GameDataService: Creating CharacterCreationData...");
                CharacterCreationData result = new CharacterCreationData(randomAvatars, randomTechniques);
                System.out.println("GameDataService: CharacterCreationData created successfully");
                return result;
            }
        } catch (Exception e) {
            System.err.println("GameDataService: Error getting character creation data: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get character creation data", e);
        }
    }

    /**
     * Lấy một danh sách các avatar ngẫu nhiên.
     * 
     * @param count Số lượng avatar cần lấy.
     * @return Một danh sách avatar.
     */
    public List<Avatar> getRandomAvatars(int count) {
        System.out.println("GameDataService: Getting " + count + " random avatars");
        
        // 1. Lấy tất cả avatar từ database
        List<Avatar> allAvatars = avatarRepository.findAll();
        System.out.println("GameDataService: Found " + allAvatars.size() + " avatars in database");

        // 2. Nếu tổng số avatar ít hơn số lượng yêu cầu, trả về tất cả
        if (allAvatars.size() <= count) {
            System.out.println("GameDataService: Returning all " + allAvatars.size() + " avatars");
            return allAvatars;
        }

        // 3. Xáo trộn danh sách một cách ngẫu nhiên
        Collections.shuffle(allAvatars);

        // 4. Lấy 'count' phần tử đầu tiên từ danh sách đã xáo trộn
        List<Avatar> result = allAvatars.subList(0, count);
        System.out.println("GameDataService: Returning " + result.size() + " random avatars");
        return result;
    }

    public List<Technique> getRandomTechniques(int count) {
        System.out.println("GameDataService: Getting " + count + " random techniques");
        
        // Gọi thẳng đến phương thức mới trong repository, hiệu quả hơn rất nhiều!
        List<Technique> result = techniqueRepository.findRandomTechniques(count);
        System.out.println("GameDataService: Found " + result.size() + " techniques in database");
        return result;
    }
    
    // Getter để test endpoint có thể truy cập sessionRepository
    public CharacterCreationSessionRepository getSessionRepository() {
        return sessionRepository;
    }
}