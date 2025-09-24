package com.toicuongtong.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "character_creation_sessions", schema = "tct")
@Data
@NoArgsConstructor
public class CharacterCreationSession {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "session_data", columnDefinition = "TEXT")
    private String sessionData;

    public CharacterCreationSession(Long userId, String sessionData) {
        this.userId = userId;
        this.sessionData = sessionData;
    }
}