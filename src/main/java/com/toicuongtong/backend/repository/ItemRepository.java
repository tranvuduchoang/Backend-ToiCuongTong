package com.toicuongtong.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.toicuongtong.backend.model.Item;
import com.toicuongtong.backend.model.Rarity;

/**
 * Repository cho Item
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    Optional<Item> findByCode(String code);
    
    List<Item> findByCategory(Item.ItemCategory category);
    
    List<Item> findByRarity(Rarity rarity);
    
    @Query("SELECT i FROM Item i WHERE i.category = :category AND i.rarity = :rarity")
    List<Item> findByCategoryAndRarity(@Param("category") Item.ItemCategory category, 
                                      @Param("rarity") Rarity rarity);
    
    @Query("SELECT i FROM Item i WHERE i.isUnique = true")
    List<Item> findUniqueItems();
}
