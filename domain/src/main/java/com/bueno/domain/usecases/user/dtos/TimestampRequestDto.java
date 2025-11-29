package com.bueno.domain.usecases.user.dtos;

import java.time.Instant;
import java.util.UUID;

public record TimestampRequestDto(UUID gameUuid, Instant timestamp) {
}
