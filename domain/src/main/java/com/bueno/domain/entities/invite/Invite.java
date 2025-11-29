package com.bueno.domain.entities.invite;

import com.bueno.domain.usecases.invite.dtos.InviteDto;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

public class Invite {
    private final UUID uuid;
    private final UUID hostPlayerUuid;
    private final UUID invitedPlayerUuid;
    private final Instant expiresAt;

    public Invite(UUID uuid, UUID hostPlayerUuid, UUID invitedPlayerUuid, Instant expiresAt) {
        this.uuid = Objects.requireNonNull(uuid);
        this.hostPlayerUuid = Objects.requireNonNull(hostPlayerUuid);
        this.invitedPlayerUuid = Objects.requireNonNull(invitedPlayerUuid);
        this.expiresAt = Objects.requireNonNull(expiresAt);
    }

    public static Invite of(UUID hostPlayerUuid, UUID invitedPlayerUuid) {
        Instant expirationTime = Instant.now().plus(2, ChronoUnit.MINUTES);

        return new Invite(
                UUID.randomUUID(),
                hostPlayerUuid,
                invitedPlayerUuid,
                expirationTime
        );
    }

    public static Invite with(
            UUID uuid,
            UUID hostPlayerUuid,
            UUID invitedPlayerUuid,
            Instant expiresAt) {
        return new Invite(
                uuid,
                hostPlayerUuid,
                invitedPlayerUuid,
                expiresAt
        );
    }

    public InviteDto toDto() {
        return new InviteDto(
                this.getUuid(),
                this.getHostPlayerUuid(),
                this.getInvitedPlayerUuid(),
                this.getExpiresAt()
        );
    }

    public static Invite fromDto(InviteDto dto) {
        Objects.requireNonNull(dto, "InviteDto cannot be null");
        return Invite.with(
                dto.uuid(),
                dto.hostPlayerUuid(),
                dto.invitedPlayerUuid(),
                dto.expiresAt()
        );
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getHostPlayerUuid() {
        return hostPlayerUuid;
    }

    public UUID getInvitedPlayerUuid() {
        return invitedPlayerUuid;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}