package com.toicuongtong.backend.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Composite Key cho MapTile
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapTileId implements Serializable {
    private Long runId;
    private Integer x;
    private Integer y;
}
