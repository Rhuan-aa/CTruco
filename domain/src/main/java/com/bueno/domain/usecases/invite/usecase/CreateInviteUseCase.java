package com.bueno.domain.usecases.invite.usecase;

import com.bueno.domain.entities.invite.Invite;
import com.bueno.domain.usecases.invite.dtos.InviteDto;
import com.bueno.domain.usecases.invite.repos.InviteRepository;
import com.bueno.domain.usecases.session.usecase.FindSessionUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class CreateInviteUseCase {

    private final InviteRepository inviteRepo;
    private final FindSessionUseCase findSessionUseCase;

    @Autowired
    public CreateInviteUseCase(InviteRepository inviteRepo, FindSessionUseCase findSessionUseCase) {
        this.inviteRepo = inviteRepo;
        this.findSessionUseCase = findSessionUseCase;
    }

    public InviteDto createGameInvite(UUID hostPlayerUuid, UUID invitedPlayerUuid) {
        Objects.requireNonNull(hostPlayerUuid, "Host's uuid can't be null");
        Objects.requireNonNull(invitedPlayerUuid, "Invited player's uuid can't be null");

        if (hostPlayerUuid.equals(invitedPlayerUuid)) {
            throw new
                    IllegalArgumentException("A player can't invite him self");
        }

        if (!findSessionUseCase.isPlayerInvitable(hostPlayerUuid, invitedPlayerUuid))
            throw new IllegalArgumentException("This player isn't able to receive invites");

        final var invite = Invite.of(hostPlayerUuid, invitedPlayerUuid).toDto();
        inviteRepo.save(invite);
        return invite;
    }
}
