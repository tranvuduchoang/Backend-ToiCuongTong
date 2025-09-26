package com.toicuongtong.backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toicuongtong.backend.dto.CreateCharacterRequest;
import com.toicuongtong.backend.dto.PlayerDTO;
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
        Player player = playerRepository.findByUser_Id(userId).orElse(null);
        if (player == null) {
            return false; // Chưa có Player nghĩa là chưa tạo nhân vật
        }
        return player.isCharacterCreated();
    }

    public PlayerDTO getPlayerData(Long userId) {
        System.out.println("PlayerService: getPlayerData called for user ID: " + userId);
        try {
            var playerOpt = playerRepository.findByUser_Id(userId);
            if (playerOpt.isPresent()) {
                Player player = playerOpt.get();
                System.out.println("PlayerService: Player found - ID: " + player.getId() + ", Name: " + player.getName());
                
                // Convert Player to PlayerDTO
                PlayerDTO playerDTO = new PlayerDTO();
                playerDTO.setId(player.getId());
                playerDTO.setName(player.getName());
                playerDTO.setAvatarUrl(player.getAvatarUrl());
                playerDTO.setCharacterCreated(player.isCharacterCreated());
                playerDTO.setStats(player.getStats());
                playerDTO.setUserId(player.getUser().getId()); // Lấy user ID thay vì toàn bộ user object
                
                // Thêm các trường mới
                playerDTO.setCurrentRealmId(player.getCurrentRealmId());
                playerDTO.setCurrentSublevel(player.getCurrentSublevel());
                playerDTO.setExperience(player.getExperience());
                playerDTO.setMaxExperience(player.getMaxExperience());
                playerDTO.setSpiritStones(player.getSpiritStones());
                playerDTO.setGold(player.getGold());
                playerDTO.setReputation(player.getReputation());
                playerDTO.setCurrentStamina(player.getCurrentStamina());
                playerDTO.setMaxStamina(player.getMaxStamina());
                
                // Tính toán cultivation level và realm
                if (player.getCurrentRealmId() != null) {
                    // Lấy tên realm từ database hoặc hardcode
                    String[] realmNames = {"Luyện Thể", "Luyện Khí", "Trúc Cơ", "Kết Đan", "Nguyên Anh", "Hóa Thần"};
                    if (player.getCurrentRealmId() <= realmNames.length) {
                        playerDTO.setCultivationLevel(realmNames[player.getCurrentRealmId() - 1]);
                    }
                } else {
                    playerDTO.setCultivationLevel("Luyện Thể");
                }
                
                playerDTO.setCultivationRealm("Tầng " + Objects.requireNonNullElse(player.getCurrentSublevel(), 1));
                
                return playerDTO;
            } else {
                System.out.println("PlayerService: No player found for user ID: " + userId);
                // Trả về null thay vì ném exception để frontend có thể xử lý
                return null;
            }
        } catch (Exception e) {
            System.out.println("PlayerService: Error in getPlayerData: " + e.getMessage());
            // Log error for debugging - consider using proper logging framework
            throw e;
        }
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
        Player player = playerRepository.findByUser_Id(userId).orElse(null);
        
        if (player == null) {
            // Tạo Player mới nếu chưa tồn tại
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));
            
            player = new Player();
            player.setUser(user);
            player.setName(user.getDisplayName());
            
            // Khởi tạo các giá trị mặc định cho các trường bắt buộc
            player.setCurrentRealmId(1); // Bắt đầu từ realm 1 (Luyện Thể)
            player.setCurrentSublevel(1); // Bắt đầu từ sublevel 1
            player.setExperience(0);
            player.setMaxExperience(2000); // Exp cần để level up
            player.setSpiritStones(0);
            player.setGold(0);
            player.setReputation(0);
            player.setCurrentStamina(100);
            player.setMaxStamina(100);
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
    
    /**
     * Lấy player theo ID
     */
    public Player getPlayerById(Long playerId) {
        return playerRepository.findById(playerId).orElse(null);
    }
    
    /**
     * Cập nhật player
     */
    public Player updatePlayer(Player player) {
        return playerRepository.save(player);
    }
    
    /**
     * Hồi phục stamina theo thời gian
     */
    public void recoverStamina(Long playerId) {
        Player player = getPlayerById(playerId);
        if (player == null) return;
        
        // Basic stamina recovery for UI testing
        // Recover 10 stamina per call (can be called every few minutes)
        int recoveryAmount = 10;
        int newStamina = Math.min(player.getMaxStamina(), player.getCurrentStamina() + recoveryAmount);
        player.setCurrentStamina(newStamina);
        updatePlayer(player);
        
        System.out.println("Player " + player.getName() + " recovered " + recoveryAmount + " stamina. Current: " + newStamina);
    }
    
    /**
     * Kiểm tra và xử lý level up
     */
    public boolean checkAndProcessLevelUp(Long playerId) {
        Player player = getPlayerById(playerId);
        if (player == null) return false;
        
        // Basic level up logic for UI testing
        int currentExp = player.getExperience();
        int maxExp = player.getMaxExperience();
        
        if (currentExp >= maxExp) {
            // Level up!
            player.setExperience(currentExp - maxExp);
            player.setCurrentSublevel(player.getCurrentSublevel() + 1);
            player.setMaxStamina(player.getMaxStamina() + 10); // Increase max stamina
            player.setCurrentStamina(player.getMaxStamina()); // Full stamina on level up
            
            // Increase stats slightly
            Map<String, Object> stats = player.getStats();
            if (stats != null) {
                stats.put("STR", (Integer) stats.getOrDefault("STR", 10) + 1);
                stats.put("AGI", (Integer) stats.getOrDefault("AGI", 10) + 1);
                stats.put("DEF", (Integer) stats.getOrDefault("DEF", 10) + 1);
            }
            
            updatePlayer(player);
            System.out.println("Player " + player.getName() + " leveled up to sublevel " + player.getCurrentSublevel());
            return true;
        }
        
        return false;
    }
    
    /**
     * Cập nhật rewards sau combat
     */
    public PlayerDTO updateRewards(Long userId, Map<String, Object> rewards) {
        System.out.println("=== updateRewards called ===");
        System.out.println("User ID: " + userId);
        System.out.println("Rewards: " + rewards);
        
        // Tìm player theo user_id thay vì player_id
        Optional<Player> playerOpt = playerRepository.findByUser_Id(userId);
        if (playerOpt.isEmpty()) {
            System.out.println("Player not found for user ID: " + userId);
            return null;
        }
        
        Player player = playerOpt.get();
        
        System.out.println("Found player ID: " + player.getId());
        
        System.out.println("Before update - Experience: " + player.getExperience() + ", Gold: " + player.getGold());
        
        // Update experience
        if (rewards.containsKey("experience")) {
            int expGain = (Integer) rewards.get("experience");
            player.setExperience(player.getExperience() + expGain);
            System.out.println("Added experience: " + expGain + ", New total: " + player.getExperience());
        }
        
        // Update gold
        if (rewards.containsKey("gold")) {
            int goldGain = (Integer) rewards.get("gold");
            player.setGold(player.getGold() + goldGain);
            System.out.println("Added gold: " + goldGain + ", New total: " + player.getGold());
        }
        
        // Update spirit stones
        if (rewards.containsKey("spiritStones")) {
            int stonesGain = (Integer) rewards.get("spiritStones");
            player.setSpiritStones(player.getSpiritStones() + stonesGain);
            System.out.println("Added spirit stones: " + stonesGain + ", New total: " + player.getSpiritStones());
        }
        
        // Save updated player
        Player savedPlayer = updatePlayer(player);
        System.out.println("After save - Experience: " + savedPlayer.getExperience() + ", Gold: " + savedPlayer.getGold());
        
        // Return updated player data using the same user ID
        PlayerDTO result = getPlayerData(userId);
        System.out.println("Returned DTO - Experience: " + result.getExperience() + ", Gold: " + result.getGold());
        return result;
    }
}