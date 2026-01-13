package com.bueno.domain.usecases.invite.dtos;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record InviteDto(
        UUID uuid,
        UUID hostPlayerUuid,
        UUID invitedPlayerUuid,
        Instant expiresAt
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InviteDto inviteDto = (InviteDto) o;
        return Objects.equals(uuid, inviteDto.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
