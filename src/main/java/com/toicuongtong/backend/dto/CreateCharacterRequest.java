package com.toicuongtong.backend.dto;

import java.util.List;
import java.util.Map;

public record CreateCharacterRequest(
    String avatarUrl,
    List<Long> techniqueIds,
    Map<String, Integer> stats
) {}