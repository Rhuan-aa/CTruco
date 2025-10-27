package com.bueno.controllers;

import com.bueno.domain.entities.intel.Intel;
import com.bueno.domain.usecases.game.converter.GameConverter;
import com.bueno.domain.usecases.game.repos.GameRepository;
import com.bueno.domain.usecases.hand.PlayCardUseCase;
import com.bueno.domain.usecases.hand.dtos.PlayCardDto;
import com.bueno.domain.usecases.intel.HandleIntelUseCase;
import com.bueno.domain.usecases.intel.dtos.CardDto;
import com.bueno.domain.usecases.intel.dtos.IntelDto;
import com.bueno.domain.usecases.intel.dtos.IntelSinceDto;
import com.bueno.domain.usecases.user.dtos.CardWebsocketRequestDto;
import com.bueno.domain.usecases.utils.exceptions.GameNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.UUID;

@Controller
public class WebSocketCardController {

    private final PlayCardUseCase playCardUseCase;
    private final HandleIntelUseCase intelUseCase;
    private final GameRepository gameRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketCardController(RabbitTemplate rabbitTemplate, PlayCardUseCase playCardUseCase, HandleIntelUseCase intelUseCase, GameRepository gameRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.playCardUseCase = playCardUseCase;
        this.intelUseCase = intelUseCase;
        this.gameRepository = gameRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/games/players/{uuid}/cards/played")
    public void play(@DestinationVariable UUID uuid, CardWebsocketRequestDto request) {
        final var requestModel = new PlayCardDto(uuid, request.card());
        playCardUseCase.playCard(requestModel);
        var payload = intelUseCase.findIntelSince(uuid, request.timestamp());
        simpMessagingTemplate.convertAndSend(getDestination(uuid), payload);
    }

    @MessageMapping("/games/players/{uuid}/cards/discarded")
    public void discard(@DestinationVariable UUID uuid, CardWebsocketRequestDto request) {
        final var requestModel = new PlayCardDto(uuid, request.card());
        playCardUseCase.discard(requestModel);
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
