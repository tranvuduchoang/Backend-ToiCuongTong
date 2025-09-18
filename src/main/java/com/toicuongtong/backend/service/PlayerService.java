package com.toicuongtong.backend.service;

import com.toicuongtong.backend.dto.CreateCharacterRequest;
import com.toicuongtong.backend.model.Player;
import com.toicuongtong.backend.model.PlayerTechnique;
import com.toicuongtong.backend.model.Technique;
import com.toicuongtong.backend.repository.CharacterCreationSessionRepository;
import com.toicuongtong.backend.repository.PlayerRepository;
import com.toicuongtong.backend.repository.PlayerTechniqueRepository;
import com.toicuongtong.backend.repository.TechniqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TechniqueRepository techniqueRepository;
    private final PlayerTechniqueRepository playerTechniqueRepository;
    private final CharacterCreationSessionRepository sessionRepository;

    public boolean getCharacterCreationStatus(Long userId) {
        Player player = playerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người chơi tương ứng với user ID: " + userId));
        return player.isCharacterCreated();
    }

    @Transactional
    public Player createCharacter(Long userId, CreateCharacterRequest request) {
        // --- LOGIC VALIDATION ĐÃ ĐƯỢC SỬA LẠI ---
        final Map<String, Integer> BASE_STATS = Map.of("STR", 5, "AGI", 5, "DEF", 5);
        final int MAX_SP = 20;

        // Tính toán số điểm SP đã được cộng thêm từ các chỉ số cơ bản
        int spUsed = 0;
        for (Map.Entry<String, Integer> entry : request.stats().entrySet()) {
            String statName = entry.getKey();
            Integer finalValue = entry.getValue();

            // Chỉ tính các chỉ số có trong bộ chỉ số cơ bản có thể cộng điểm
            if (BASE_STATS.containsKey(statName)) {
                int baseValue = BASE_STATS.get(statName);
                if (finalValue < baseValue) {
                    throw new IllegalArgumentException("Chỉ số " + statName + " không thể nhỏ hơn giá trị cơ bản.");
                }
                spUsed += (finalValue - baseValue);
            }
        }

        if (spUsed != MAX_SP) {
            throw new IllegalArgumentException("Vui lòng sử dụng chính xác " + MAX_SP + " SP!");
        }
        // ---------------------------------------------

        if (request.techniqueIds().size() != 2) {
            throw new IllegalArgumentException("Vui lòng chọn đúng 2 công pháp.");
        }

        Player player = playerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người chơi tương ứng với user ID: " + userId));

        // Cập nhật thông tin nhân vật
        player.setAvatarUrl(request.avatarUrl());

        Map<String, Object> statsObjectMap = new HashMap<>(request.stats());
        player.setStats(statsObjectMap);

        player.setCharacterCreated(true);

        // Thêm bước dọn dẹp công pháp cũ để đảm bảo an toàn nếu người chơi thử lại
        // nhiều lần
        // playerTechniqueRepository.deleteAllByPlayerId(player.getId());

        // Gán các công pháp đã chọn cho người chơi
        List<Technique> chosenTechniques = techniqueRepository.findAllById(request.techniqueIds());
        for (Technique tech : chosenTechniques) {
            PlayerTechnique playerTechnique = new PlayerTechnique();
            playerTechnique.setPlayer(player);
            playerTechnique.setTechnique(tech);
            playerTechnique.setMastery(0);
            playerTechniqueRepository.save(playerTechnique);
        }

        Player savedPlayer = playerRepository.save(player);

        // --- BƯỚC MỚI ĐƯỢC THÊM VÀO ---
        // Sau khi tất cả các bước trên đã thành công, xóa session tạm đi.
        // Chúng ta dùng ID của player để tìm và xóa session tương ứng.
        sessionRepository.deleteById(savedPlayer.getId());

        return savedPlayer;
    }
}