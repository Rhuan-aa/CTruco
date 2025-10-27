package com.bueno.controllers;

import com.bueno.domain.usecases.game.converter.GameConverter;
import com.bueno.domain.usecases.game.repos.GameRepository;
import com.bueno.domain.usecases.hand.PointsProposalUseCase;
import com.bueno.domain.usecases.intel.HandleIntelUseCase;
import com.bueno.domain.usecases.intel.dtos.IntelDto;
import com.bueno.domain.usecases.intel.dtos.IntelSinceDto;
import com.bueno.domain.usecases.user.dtos.TimestampRequestDto;
import com.bueno.domain.usecases.utils.exceptions.GameNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.UUID;

@Controller
public class WebSocketPointsController {

    private final HandleIntelUseCase intelUseCase;
    private final PointsProposalUseCase pointsUseCase;
    private final GameRepository gameRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketPointsController(HandleIntelUseCase intelUseCase, PointsProposalUseCase pointsUseCase, GameRepository gameRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.intelUseCase = intelUseCase;
        this.pointsUseCase = pointsUseCase;
        this.gameRepository = gameRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/games/players/{uuid}/raised-points")
    public void raise(@DestinationVariable UUID uuid, TimestampRequestDto request) {
        pointsUseCase.raise(uuid);
        var payload = intelUseCase.findIntelSince(uuid, request.timestamp());
        simpMessagingTemplate.convertAndSend(getDestination(uuid), payload);
    }

    @MessageMapping("/games/players/{uuid}/accepted-bet")
    public void accept(@DestinationVariable UUID uuid, TimestampRequestDto request) {
        pointsUseCase.accept(uuid);
        var payload = intelUseCase.findIntelSince(uuid, request.timestamp());
        simpMessagingTemplate.convertAndSend(getDestination(uuid), payload);
    }

    @MessageMapping("/games/players/{uuid}/quit-hand")
    public void quit(@DestinationVariable UUID uuid, TimestampRequestDto request) {
        pointsUseCase.quit(uuid);
        var payload = intelUseCase.findIntelSince(uuid, request.timestamp());
        simpMessagingTemplate.convertAndSend(getDestination(uuid), payload);
    }

    private @NotNull String getDestination(UUID uuid) {
        return "/topic/game/" +
                gameRepository
                        .findByPlayerUuid(uuid)
                        .map(GameConverter::fromDto)
                        .orElseThrow(
                                () -> new GameNotFoundException(
                                        "User with UUID " +
                                                uuid +
                                                " is not in an active game.")
                        )
                        .getUuid()
                        .toString();
    }
}
