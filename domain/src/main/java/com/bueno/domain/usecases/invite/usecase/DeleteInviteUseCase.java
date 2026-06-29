package com.bueno.domain.usecases.invite.usecase;

import com.bueno.domain.usecases.invite.repos.InviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class DeleteInviteUseCase {

    final InviteRepository inviteRepo;

    @Autowired
    public DeleteInviteUseCase(InviteRepository inviteRepo) {
        this.inviteRepo = inviteRepo;
    }

    public void deleteInviteByUuid(UUID inviteUuid) {
        Objects.requireNonNull(inviteUuid, "Invite's uuid can't be null");
        inviteRepo.deleteByInviteUuid(inviteUuid);
    }
}
