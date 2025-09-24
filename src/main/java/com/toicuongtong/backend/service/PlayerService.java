package com.toicuongtong.backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toicuongtong.backend.dto.CreateCharacterRequest;
import com.toicuongtong.backend.model.Player;
import com.toicuongtong.backend.model.PlayerTechnique;
import com.toicuongtong.backend.model.Technique;
import com.toicuongtong.backend.model.User;
import com.toicuongtong.backend.repository.PlayerRepository;
import com.toicuongtong.backend.repository.PlayerTechniqueRepository;
import com.toicuongtong.backend.repository.TechniqueRepository;
import com.toicuongtong.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TechniqueRepository techniqueRepository;
    private final PlayerTechniqueRepository playerTechniqueRepository;
    private final UserRepository userRepository;

    public boolean getCharacterCreationStatus(Long userId) {
        Player player = playerRepository.findByUserId(userId).orElse(null);
        if (player == null) {
            return false; // Chưa có Player nghĩa là chưa tạo nhân vật
        }
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

        // Kiểm tra xem Player đã tồn tại chưa
        Player player = playerRepository.findByUserId(userId).orElse(null);
        
        if (player == null) {
            // Tạo Player mới nếu chưa tồn tại
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));
            
            player = new Player();
            player.setUser(user);
            player.setName(user.getDisplayName());
        }

        // Cập nhật thông tin nhân vật
        player.setAvatarUrl(request.avatarUrl());

        Map<String, Object> statsObjectMap = new HashMap<>(request.stats());
        player.setStats(statsObjectMap);

        player.setCharacterCreated(true);

        // Lưu Player trước để có ID
        Player savedPlayer = playerRepository.save(player);

        // Thêm bước dọn dẹp công pháp cũ để đảm bảo an toàn nếu người chơi thử lại
        // nhiều lần
        playerTechniqueRepository.deleteAllByPlayer(savedPlayer);

        // Gán các công pháp đã chọn cho người chơi
        List<Technique> chosenTechniques = techniqueRepository.findAllById(request.techniqueIds());
        for (Technique tech : chosenTechniques) {
            PlayerTechnique playerTechnique = new PlayerTechnique();
            playerTechnique.setPlayer(savedPlayer);
            playerTechnique.setTechnique(tech);
            playerTechnique.setMastery(0);
            playerTechniqueRepository.save(playerTechnique);
        }

        // --- KHÔNG XÓA SESSION ---
        // Session sẽ được giữ lại để user có thể reload trang và thấy cùng bộ lựa chọn
        // Session chỉ bị xóa khi user tạo nhân vật thành công và chuyển sang trang game
        // sessionRepository.deleteById(savedPlayer.getId());

        return savedPlayer;
    }
}