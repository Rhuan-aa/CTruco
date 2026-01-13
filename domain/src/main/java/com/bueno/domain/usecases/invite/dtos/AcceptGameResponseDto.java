package com.bueno.domain.usecases.invite.dtos;

import com.bueno.domain.usecases.intel.dtos.IntelDto;
import com.bueno.domain.usecases.invite.utils.ResponseType;

import java.util.UUID;

public record AcceptGameResponseDto(IntelDto intel, ResponseType responseType) {
}
