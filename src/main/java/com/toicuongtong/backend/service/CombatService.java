package com.toicuongtong.backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toicuongtong.backend.dto.CombatActionDTO;
import com.toicuongtong.backend.dto.CombatDTO;
import com.toicuongtong.backend.model.MonsterDef;
import com.toicuongtong.backend.model.MonsterDrop;
import com.toicuongtong.backend.model.MonsterSkills;
import com.toicuongtong.backend.model.Player;
import com.toicuongtong.backend.repository.MonsterDefRepository;
import com.toicuongtong.backend.repository.MonsterDropRepository;
import com.toicuongtong.backend.repository.MonsterSkillsRepository;

/**
 * Service cho Combat System
 */
@Service
@Transactional
public class CombatService {
    
    @Autowired
    private MonsterDefRepository monsterDefRepository;
    
    @Autowired
    private MonsterSkillsRepository monsterSkillsRepository;
    
    @Autowired
    private MonsterDropRepository monsterDropRepository;
    
    @Autowired
    private PlayerService playerService;
    
    // TODO: Implement item usage in combat
    // @Autowired
    // private ItemRepository itemRepository;
    
    // Cache cho combat sessions
    private final Map<Long, CombatDTO> activeCombats = new HashMap<>();
    
    /**
     * Bắt đầu trận chiến
     */
    public CombatDTO startCombat(Long playerId, Long monsterId) {
        // Kiểm tra player
        var player = playerService.getPlayerById(playerId);
        if (player == null) {
            throw new RuntimeException("Player không tồn tại");
        }
        
        // Kiểm tra monster
        Optional<MonsterDef> monsterOpt = monsterDefRepository.findById(monsterId);
        if (monsterOpt.isEmpty()) {
            throw new RuntimeException("Monster không tồn tại");
        }
        
        MonsterDef monster = monsterOpt.get();
        
        // Tạo combat session
        CombatDTO combat = new CombatDTO();
        combat.setCombatId(System.currentTimeMillis());
        combat.setPlayerId(playerId);
        combat.setPlayerName(player.getName());
        combat.setMonsterId(monsterId);
        combat.setMonsterName(monster.getName());
        combat.setCombatStatus("active");
        combat.setCurrentTurn(1);
        combat.setMaxTurns(50);
        combat.setActionHistory(new ArrayList<>());
        
        // Tạo player participant
        CombatDTO.CombatParticipantDTO playerParticipant = createPlayerParticipant(player);
        combat.setPlayer(playerParticipant);
        
        // Tạo monster participant
        CombatDTO.CombatParticipantDTO monsterParticipant = createMonsterParticipant(monster);
        combat.setMonster(monsterParticipant);
        
        // Lưu vào cache
        activeCombats.put(combat.getCombatId(), combat);
        
        combat.setMessage("Trận chiến bắt đầu! " + player.getName() + " vs " + monster.getName());
        
        return combat;
    }
    
    /**
     * Thực hiện hành động trong combat
     */
    public CombatDTO performAction(Long combatId, String actionType, Long skillId, Long targetId) {
        CombatDTO combat = activeCombats.get(combatId);
        if (combat == null) {
            throw new RuntimeException("Combat session không tồn tại");
        }
        
        if (!"active".equals(combat.getCombatStatus())) {
            throw new RuntimeException("Combat đã kết thúc");
        }
        
        // Tạo action
        CombatActionDTO action = new CombatActionDTO();
        action.setActionId(System.currentTimeMillis());
        action.setCombatId(combatId);
        action.setActionType(actionType);
        action.setActorType("player");
        action.setActorId(combat.getPlayerId());
        action.setActorName(combat.getPlayerName());
        action.setTargetId(targetId);
        action.setTimestamp(System.currentTimeMillis());
        
        // Xử lý hành động
        String result = processPlayerAction(combat, action, skillId);
        action.setResult(result);
        
        // Thêm vào lịch sử
        combat.getActionHistory().add(action);
        
        // Kiểm tra kết thúc combat
        if (combat.getMonster().getCurrentHp() <= 0) {
            combat.setCombatStatus("victory");
            combat.setMessage("Bạn đã chiến thắng!");
            processVictory(combat);
        } else if (combat.getPlayer().getCurrentHp() <= 0) {
            combat.setCombatStatus("defeat");
            combat.setMessage("Bạn đã thất bại!");
        } else {
            // Monster turn
            processMonsterTurn(combat);
            
            if (combat.getPlayer().getCurrentHp() <= 0) {
                combat.setCombatStatus("defeat");
                combat.setMessage("Bạn đã thất bại!");
            } else {
                combat.setCurrentTurn(combat.getCurrentTurn() + 1);
                combat.setMessage("Lượt " + combat.getCurrentTurn() + " - " + action.getMessage());
            }
        }
        
        return combat;
    }
    
    /**
     * Thoát khỏi combat
     */
    public CombatDTO fleeCombat(Long combatId) {
        CombatDTO combat = activeCombats.get(combatId);
        if (combat == null) {
            throw new RuntimeException("Combat session không tồn tại");
        }
        
        combat.setCombatStatus("fled");
        combat.setMessage("Bạn đã chạy trốn!");
        
        // Xóa khỏi cache
        activeCombats.remove(combatId);
        
        return combat;
    }
    
    /**
     * Lấy thông tin combat
     */
    public CombatDTO getCombat(Long combatId) {
        return activeCombats.get(combatId);
    }
    
    /**
     * Tạo player participant
     */
    private CombatDTO.CombatParticipantDTO createPlayerParticipant(Player player) {
        CombatDTO.CombatParticipantDTO participant = new CombatDTO.CombatParticipantDTO();
        participant.setId(player.getId());
        participant.setName(player.getName());
        
        // Tính toán HP/MP từ stats
        Map<String, Object> stats = player.getStats();
        int baseHp = 100;
        int baseMp = 50;
        
        if (stats != null) {
            baseHp += (Integer) stats.getOrDefault("STR", 0) * 10;
            baseMp += (Integer) stats.getOrDefault("AGI", 0) * 5;
        }
        
        participant.setCurrentHp(baseHp);
        participant.setMaxHp(baseHp);
        participant.setCurrentMp(baseMp);
        participant.setMaxMp(baseMp);
        participant.setStats(stats);
        participant.setAvailableSkills(new ArrayList<>());
        participant.setActiveEffects(new ArrayList<>());
        
        return participant;
    }
    
    /**
     * Tạo monster participant
     */
    private CombatDTO.CombatParticipantDTO createMonsterParticipant(MonsterDef monster) {
        CombatDTO.CombatParticipantDTO participant = new CombatDTO.CombatParticipantDTO();
        participant.setId(monster.getId());
        participant.setName(monster.getName());
        
        Map<String, Object> stats = monster.getStats();
        int hp = (Integer) stats.getOrDefault("HP", 100);
        int mp = 50; // Monsters có MP cố định
        
        participant.setCurrentHp(hp);
        participant.setMaxHp(hp);
        participant.setCurrentMp(mp);
        participant.setMaxMp(mp);
        participant.setStats(stats);
        
        // Lấy skills của monster
        List<MonsterSkills> monsterSkills = monsterSkillsRepository.findByMonsterId(monster.getId());
        List<CombatDTO.CombatSkillDTO> skills = monsterSkills.stream()
            .map(ms -> {
                CombatDTO.CombatSkillDTO skill = new CombatDTO.CombatSkillDTO();
                skill.setSkillId(ms.getSkillId());
                skill.setCostMp(0); // Sẽ lấy từ Skill entity
                skill.setCdTurns(0);
                skill.setCurrentCooldown(0);
                skill.setCanUse(true);
                return skill;
            })
            .collect(Collectors.toList());
        
        participant.setAvailableSkills(skills);
        participant.setActiveEffects(new ArrayList<>());
        
        return participant;
    }
    
    /**
     * Xử lý hành động của player
     */
    private String processPlayerAction(CombatDTO combat, CombatActionDTO action, Long skillId) {
        // combat, action, skillId parameters used for processing player actions
        switch (action.getActionType()) {
            case "attack":
                return processAttack(combat, action);
            case "skill":
                return processSkill(combat, action, skillId);
            case "item":
                return processItem(combat, action);
            default:
                return "Hành động không hợp lệ";
        }
    }
    
    /**
     * Xử lý tấn công cơ bản
     */
    private String processAttack(CombatDTO combat, CombatActionDTO action) {
        CombatDTO.CombatParticipantDTO player = combat.getPlayer();
        CombatDTO.CombatParticipantDTO monster = combat.getMonster();
        
        // Tính damage
        int playerStr = (Integer) player.getStats().getOrDefault("STR", 10);
        int monsterDef = (Integer) monster.getStats().getOrDefault("DEF", 5);
        
        int damage = Math.max(1, playerStr - monsterDef + new Random().nextInt(5));
        
        // Áp dụng damage
        monster.setCurrentHp(Math.max(0, monster.getCurrentHp() - damage));
        
        action.setDamage(damage);
        action.setMessage(player.getName() + " tấn công " + monster.getName() + " gây " + damage + " sát thương!");
        
        return "hit";
    }
    
    /**
     * Xử lý kỹ năng
     */
    private String processSkill(CombatDTO combat, CombatActionDTO action, Long skillId) {
        // Basic skill implementation for UI testing
        CombatDTO.CombatParticipantDTO player = combat.getPlayer();
        CombatDTO.CombatParticipantDTO monster = combat.getMonster();
        
        // Simple skill damage calculation
        int skillDamage = 15; // Base skill damage
        int playerStr = (Integer) player.getStats().getOrDefault("STR", 10);
        skillDamage += playerStr * 2; // Scale with STR
        
        monster.setCurrentHp(Math.max(0, monster.getCurrentHp() - skillDamage));
        action.setDamage(skillDamage);
        action.setMessage(player.getName() + " sử dụng kỹ năng gây " + skillDamage + " sát thương!");
        
        return "hit";
    }
    
    /**
     * Xử lý sử dụng item
     */
    private String processItem(CombatDTO combat, CombatActionDTO action) {
        // Basic item implementation for UI testing
        CombatDTO.CombatParticipantDTO player = combat.getPlayer();
        
        // Simple healing item
        int healAmount = 30;
        int newHp = Math.min(player.getMaxHp(), player.getCurrentHp() + healAmount);
        player.setCurrentHp(newHp);
        
        action.setHealing(healAmount);
        action.setMessage(player.getName() + " sử dụng vật phẩm hồi " + healAmount + " HP!");
        
        return "heal";
    }
    
    /**
     * Xử lý lượt của monster
     */
    private void processMonsterTurn(CombatDTO combat) {
        CombatDTO.CombatParticipantDTO monster = combat.getMonster();
        CombatDTO.CombatParticipantDTO player = combat.getPlayer();
        
        // Monster tấn công cơ bản
        int monsterAtk = (Integer) monster.getStats().getOrDefault("ATK", 10);
        int playerDef = (Integer) player.getStats().getOrDefault("DEF", 5);
        
        int damage = Math.max(1, monsterAtk - playerDef + new Random().nextInt(3));
        
        player.setCurrentHp(Math.max(0, player.getCurrentHp() - damage));
        
        // Tạo action cho monster
        CombatActionDTO monsterAction = new CombatActionDTO();
        monsterAction.setActionId(System.currentTimeMillis());
        monsterAction.setCombatId(combat.getCombatId());
        monsterAction.setActionType("attack");
        monsterAction.setActorType("monster");
        monsterAction.setActorId(monster.getId());
        monsterAction.setActorName(monster.getName());
        monsterAction.setTargetId(player.getId());
        monsterAction.setDamage(damage);
        monsterAction.setMessage(monster.getName() + " tấn công " + player.getName() + " gây " + damage + " sát thương!");
        monsterAction.setTimestamp(System.currentTimeMillis());
        monsterAction.setResult("hit");
        
        combat.getActionHistory().add(monsterAction);
    }
    
    /**
     * Xử lý chiến thắng
     */
    private void processVictory(CombatDTO combat) {
        // Lấy thông tin monster
        Optional<MonsterDef> monsterOpt = monsterDefRepository.findById(combat.getMonsterId());
        if (monsterOpt.isEmpty()) return;
        
        MonsterDef monster = monsterOpt.get();
        
        // Cộng XP và Gold
        var player = playerService.getPlayerById(combat.getPlayerId());
        if (player != null) {
            player.setExperience(player.getExperience() + monster.getXpReward());
            player.setGold(player.getGold() + monster.getGoldReward());
            playerService.updatePlayer(player);
        }
        
        // Xử lý drop items
        processMonsterDrops(combat, monster);
        
        // Xóa khỏi cache
        activeCombats.remove(combat.getCombatId());
    }
    
    /**
     * Xử lý rơi đồ từ monster
     */
    private void processMonsterDrops(CombatDTO combat, MonsterDef monster) {
        // combat and monster parameters used for processing drops
        // TODO: Implement drop logic using combat and monster data
        List<MonsterDrop> drops = monsterDropRepository.findByMonsterId(monster.getId());
        Random random = new Random();
        
        for (MonsterDrop drop : drops) {
            if (random.nextDouble() < drop.getDropChance()) {
                // TODO: Add item to player inventory
                System.out.println("Player nhận được item: " + drop.getItemId());
            }
        }
    }
}
