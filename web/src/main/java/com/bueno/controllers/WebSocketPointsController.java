package com.bueno.controllers;

import com.bueno.domain.usecases.game.repos.GameRepository;
import com.bueno.domain.usecases.hand.PointsProposalUseCase;
import com.bueno.domain.usecases.intel.HandleIntelUseCase;
import com.bueno.domain.usecases.intel.dtos.IntelSinceDto;
import com.bueno.domain.usecases.user.dtos.TimestampRequestDto;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class WebSocketPointsController {

    private final HandleIntelUseCase intelUseCase;
    private final PointsProposalUseCase pointsUseCase;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketPointsController(HandleIntelUseCase intelUseCase, PointsProposalUseCase pointsUseCase, SimpMessagingTemplate simpMessagingTemplate) {
        this.intelUseCase = intelUseCase;
        this.pointsUseCase = pointsUseCase;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/games/players/{uuid}/raised-points")
    public void raise(@DestinationVariable UUID uuid, TimestampRequestDto request) {
        pointsUseCase.raise(uuid);
        var payload = intelUseCase.findIntelSince(uuid, request.timestamp());
        sendMessage(request.gameUuid(),payload);
    }

    @MessageMapping("/games/players/{uuid}/accepted-bet")
    public void accept(@DestinationVariable UUID uuid, TimestampRequestDto request) {
        pointsUseCase.accept(uuid);
        var payload = intelUseCase.findIntelSince(uuid, request.timestamp());
        sendMessage(request.gameUuid(),payload);
    }

    @MessageMapping("/games/players/{uuid}/quit-hand")
    public void quit(@DestinationVariable UUID uuid, TimestampRequestDto request) {
        pointsUseCase.quit(uuid);
        var payload = intelUseCase.findIntelSince(uuid, request.timestamp());
        sendMessage(request.gameUuid(),payload);
    }

    private void sendMessage(UUID gameUuid, IntelSinceDto payload) {
        simpMessagingTemplate.convertAndSend("/topic/game/" + gameUuid + "/state", payload);
    }
}
