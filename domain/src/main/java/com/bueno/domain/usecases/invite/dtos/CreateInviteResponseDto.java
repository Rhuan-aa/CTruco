package com.bueno.domain.usecases.invite.dtos;

import com.bueno.domain.usecases.invite.utils.ResponseFor;

import java.util.UUID;

public record CreateInviteResponseDto(
        UUID inviteUuid,
        String hostUsername,
        String invitedUsername,
        ResponseFor responseFor) {
}
