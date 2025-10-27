package com.bueno.domain.usecases.user.dtos;

import com.bueno.domain.usecases.intel.dtos.CardDto;

import java.time.Instant;

public record CardWebsocketRequestDto(CardDto card, Instant timestamp) {
}
