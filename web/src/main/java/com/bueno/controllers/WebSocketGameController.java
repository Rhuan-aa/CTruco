package com.bueno.controllers;

import com.bueno.domain.usecases.game.repos.GameRepository;
import com.bueno.domain.usecases.game.usecase.RemoveGameUseCase;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
public class WebSocketGameController {

    private final RemoveGameUseCase removeGameUseCase;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameRepository gameRepository;


    public WebSocketGameController(RemoveGameUseCase removeGameUseCase, SimpMessagingTemplate simpMessagingTemplate, GameRepository gameRepository) {
        this.removeGameUseCase = removeGameUseCase;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.gameRepository = gameRepository;
    }

    @MessageMapping("/games/{gameUuid}/quit")
    public void quitGame(@DestinationVariable UUID gameUuid, UUID playerUuid) {
        if (gameRepository.findByPlayerUuid(playerUuid).isPresent()) {
            removeGameUseCase.byUserUuid(playerUuid, true);
        }
        simpMessagingTemplate.convertAndSend("/topic/game/" + gameUuid + "/quit", gameUuid);
    }
}
