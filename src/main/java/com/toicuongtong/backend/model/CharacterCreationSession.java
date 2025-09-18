package com.toicuongtong.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "character_creation_sessions", schema = "tct")
@Data
@NoArgsConstructor
public class CharacterCreationSession {

    @Id
    @Column(name = "player_id")
    private Long playerId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "session_data", columnDefinition = "jsonb")
    private Map<String, Object> sessionData;

    public CharacterCreationSession(Long playerId, Map<String, Object> sessionData) {
        this.playerId = playerId;
        this.sessionData = sessionData;
    }
}