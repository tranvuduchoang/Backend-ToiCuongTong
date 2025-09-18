package com.toicuongtong.backend.service;

import com.toicuongtong.backend.dto.CharacterCreationData;
import com.toicuongtong.backend.model.Avatar;
import com.toicuongtong.backend.model.CharacterCreationSession;
import com.toicuongtong.backend.model.Technique;
import com.toicuongtong.backend.repository.AvatarRepository;
import com.toicuongtong.backend.repository.CharacterCreationSessionRepository;
import com.toicuongtong.backend.repository.TechniqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameDataService {

    private final AvatarRepository avatarRepository;
    private final TechniqueRepository techniqueRepository;
    private final CharacterCreationSessionRepository sessionRepository;
    
    public CharacterCreationData getOrCreateCharacterCreationData(Long playerId) {
        Optional<CharacterCreationSession> existingSession = sessionRepository.findById(playerId);

        if (existingSession.isPresent()) {
            // --- TRƯỜNG HỢP 1: Người chơi đã có phiên, tải lại dữ liệu cũ ---
            Map<String, Object> sessionData = existingSession.get().getSessionData();
            List<Integer> avatarIdsInt = (List<Integer>) sessionData.get("avatarIds");
            List<Integer> techniqueIdsInt = (List<Integer>) sessionData.get("techniqueIds");
            
            List<Long> avatarIds = avatarIdsInt.stream().map(Long::valueOf).collect(Collectors.toList());
            List<Long> techniqueIds = techniqueIdsInt.stream().map(Long::valueOf).collect(Collectors.toList());

            List<Avatar> avatars = avatarRepository.findAllById(avatarIds);
            List<Technique> techniques = techniqueRepository.findAllById(techniqueIds);
            
            return new CharacterCreationData(avatars, techniques);

        } else {
            // --- TRƯỜNG HỢP 2: Người chơi mới, tạo phiên mới ---
            List<Avatar> randomAvatars = getRandomAvatars(10);
            List<Technique> randomTechniques = getRandomTechniques(5);

            // Lưu ID của các lựa chọn này vào session
            List<Long> avatarIds = randomAvatars.stream().map(Avatar::getId).collect(Collectors.toList());
            List<Long> techniqueIds = randomTechniques.stream().map(Technique::getId).collect(Collectors.toList());
            
            Map<String, Object> sessionData = Map.of(
                "avatarIds", avatarIds,
                "techniqueIds", techniqueIds
            );
            CharacterCreationSession newSession = new CharacterCreationSession(playerId, sessionData);
            sessionRepository.save(newSession);
            
            return new CharacterCreationData(randomAvatars, randomTechniques);
        }
    }

    /**
     * Lấy một danh sách các avatar ngẫu nhiên.
     * 
     * @param count Số lượng avatar cần lấy.
     * @return Một danh sách avatar.
     */
    public List<Avatar> getRandomAvatars(int count) {
        // 1. Lấy tất cả avatar từ database
        List<Avatar> allAvatars = avatarRepository.findAll();

        // 2. Nếu tổng số avatar ít hơn số lượng yêu cầu, trả về tất cả
        if (allAvatars.size() <= count) {
            return allAvatars;
        }

        // 3. Xáo trộn danh sách một cách ngẫu nhiên
        Collections.shuffle(allAvatars);

        // 4. Lấy 'count' phần tử đầu tiên từ danh sách đã xáo trộn
        return allAvatars.subList(0, count);
    }

    public List<Technique> getRandomTechniques(int count) {
        // Gọi thẳng đến phương thức mới trong repository, hiệu quả hơn rất nhiều!
        return techniqueRepository.findRandomTechniques(count);
    }
}