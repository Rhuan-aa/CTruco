package com.bueno.domain.usecases.session.dtos;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record SessionDto(
        UUID uuid,
        UUID playerUuid,
        Instant expiresAt
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionDto that = (SessionDto) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}