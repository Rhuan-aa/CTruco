package com.bueno.controllers;

import com.bueno.domain.usecases.game.repos.GameRepository;
import com.bueno.domain.usecases.hand.PlayCardUseCase;
import com.bueno.domain.usecases.hand.dtos.PlayCardDto;
import com.bueno.domain.usecases.intel.HandleIntelUseCase;
import com.bueno.domain.usecases.intel.dtos.IntelSinceDto;
import com.bueno.domain.usecases.user.dtos.CardWebsocketRequestDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class WebSocketCardController {

    private final PlayCardUseCase playCardUseCase;
    private final HandleIntelUseCase intelUseCase;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketCardController(PlayCardUseCase playCardUseCase, HandleIntelUseCase intelUseCase, SimpMessagingTemplate simpMessagingTemplate) {
        this.playCardUseCase = playCardUseCase;
        this.intelUseCase = intelUseCase;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/games/players/{uuid}/cards/played")
    public void play(@DestinationVariable UUID uuid, CardWebsocketRequestDto request) {
        final var requestModel = new PlayCardDto(uuid, request.card());
        playCardUseCase.playCard(requestModel);
        var payload = intelUseCase.findIntelSince(uuid, request.timestamp());
        sendMessage(request.gameUuid(), payload);
    }

    @MessageMapping("/games/players/{uuid}/cards/discarded")
    public void discard(@DestinationVariable UUID uuid, CardWebsocketRequestDto request) {
        final var requestModel = new PlayCardDto(uuid, request.card());
        playCardUseCase.discard(requestModel);
        var payload = intelUseCase.findIntelSince(uuid, request.timestamp());
        sendMessage(request.gameUuid(), payload);
    }

    private void sendMessage(UUID gameUuid, IntelSinceDto payload) {
        simpMessagingTemplate.convertAndSend("/topic/game/" + gameUuid + "/state", payload);
    }
}
