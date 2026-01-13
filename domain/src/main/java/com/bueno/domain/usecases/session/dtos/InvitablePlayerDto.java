package com.bueno.domain.usecases.session.dtos;

import java.util.UUID;

public record InvitablePlayerDto(UUID playerUuid, String username, int score) {
}
