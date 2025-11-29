package com.bueno.domain.usecases.user.dtos;

import com.bueno.domain.usecases.intel.dtos.CardDto;

import java.time.Instant;
import java.util.UUID;

public record CardWebsocketRequestDto(UUID gameUuid, CardDto card, Instant timestamp) {
}
