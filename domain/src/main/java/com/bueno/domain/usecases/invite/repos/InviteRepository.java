package com.bueno.domain.usecases.invite.repos;

import com.bueno.domain.usecases.invite.dtos.InviteDto;

import java.util.Optional;
import java.util.UUID;

public interface InviteRepository {
    void save(InviteDto inviteDto);
    void deleteByPlayerUuid(UUID playerUuid);
    void deleteByInviteUuid(UUID inviteUuid);
    Optional<InviteDto> findByUuid(UUID inviteUuid);
    Optional<InviteDto> findByPlayerUuid(UUID playerUuid);
}