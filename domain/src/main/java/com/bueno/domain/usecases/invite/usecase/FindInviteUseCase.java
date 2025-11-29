package com.bueno.domain.usecases.invite.usecase;

import com.bueno.domain.usecases.invite.dtos.InviteDto;
import com.bueno.domain.usecases.invite.repos.InviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class FindInviteUseCase {

    final InviteRepository inviteRepository;

    @Autowired
    public FindInviteUseCase(InviteRepository inviteRepository) {
        this.inviteRepository = inviteRepository;
    }

    public InviteDto findInviteByPlayerUuid(UUID playerUuid) {
        Objects.requireNonNull(playerUuid, "Player's uuid can't be null");
        Optional<InviteDto> inviteDto = inviteRepository.findByPlayerUuid(playerUuid);

        if (inviteDto.isEmpty())
            throw new IllegalArgumentException("This player doesn't have been invited or invited someone");

        return inviteDto.get();
    }

    public InviteDto findInviteByUuid(UUID inviteUuid) {
        Objects.requireNonNull(inviteUuid, "Invite's uuid can't be null");
        Optional<InviteDto> inviteDto = inviteRepository.findByUuid(inviteUuid);

        if (inviteDto.isEmpty())
            throw new IllegalArgumentException("This invite doesn't exists in repository");

        return inviteDto.get();
    }
}
