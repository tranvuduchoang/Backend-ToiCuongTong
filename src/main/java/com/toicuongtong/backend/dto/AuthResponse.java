package com.toicuongtong.backend.dto;

public record AuthResponse(String token, Long userId, String displayName) {}