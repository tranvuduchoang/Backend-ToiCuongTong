package com.toicuongtong.backend.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Composite Key cho MapSpawn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapSpawnId implements Serializable {
    private Long mapId;
    private Long monsterId;
}
