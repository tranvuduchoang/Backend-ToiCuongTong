package com.toicuongtong.backend.dto;

import com.toicuongtong.backend.model.Avatar;
import com.toicuongtong.backend.model.Technique;
import java.util.List;

public record CharacterCreationData(
    List<Avatar> randomAvatars,
    List<Technique> randomTechniques
) {}