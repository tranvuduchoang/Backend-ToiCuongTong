package com.toicuongtong.backend.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Composite Key cho MonsterDrop
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonsterDropId implements Serializable {
    private Long monsterId;
    private Long itemId;
}
