package com.bueno.domain.entities.session;

import com.bueno.domain.usecases.session.dtos.SessionDto;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

public class Session {
    private final UUID uuid;
    private final UUID playerUuid;
    private String accessToken;
    private Instant expiresAt;

    private Session(UUID uuid, UUID playerId, Instant expiresAt) {
        this.uuid = Objects.requireNonNull(uuid);
        this.playerUuid = Objects.requireNonNull(playerId);
        this.expiresAt = Objects.requireNonNull(expiresAt);
    }

    public static Session of(UUID playerUuid) {
        Instant expirationTime = Instant.now().plus(2, ChronoUnit.MINUTES);

        return new Session(
                UUID.randomUUID(),
                playerUuid,
                expirationTime
        );
    }

    public static Session with(
            UUID uuid,
            UUID playerUuid,
            Instant expiresAt) {
        return new Session(
                uuid,
                playerUuid,
                expiresAt
        );
    }

    public boolean isTokenValid() {
        return Instant.now().isBefore(this.expiresAt);
    }

    public void reloadExpiration() {
        this.expiresAt = Instant.now().plus(2, ChronoUnit.MINUTES);
    }

    public static SessionDto toDto(Session session) {
        return new SessionDto(
                session.getUuid(),
                session.getPlayerUuid(),
                session.getExpiresAt()
        );
    }

    public static Session fromDto(SessionDto dto) {
        return Session.with(
                dto.uuid(),
                dto.playerUuid(),
                dto.expiresAt()
        );
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    @Override
    public String toString() {
        return String.format("Session = %s (Player: %s)", uuid, playerUuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return uuid.equals(session.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}