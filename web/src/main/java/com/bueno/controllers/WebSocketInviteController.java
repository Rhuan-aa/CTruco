package com.bueno.controllers;

import com.bueno.domain.usecases.game.dtos.CreateForUserAndUserDto;
import com.bueno.domain.usecases.game.usecase.CreateGameUseCase;
import com.bueno.domain.usecases.invite.dtos.AcceptGameResponseDto;
import com.bueno.domain.usecases.invite.dtos.CreateInviteResponseDto;
import com.bueno.domain.usecases.invite.dtos.DeclineGameResponseDto;
import com.bueno.domain.usecases.invite.usecase.CreateInviteUseCase;
import com.bueno.domain.usecases.invite.usecase.DeleteInviteUseCase;
import com.bueno.domain.usecases.invite.usecase.FindInviteUseCase;
import com.bueno.domain.usecases.invite.utils.ResponseFor;
import com.bueno.domain.usecases.invite.utils.ResponseType;
import com.bueno.domain.usecases.user.UserRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class WebSocketInviteController {

    private final CreateInviteUseCase createInviteUseCase;
    private final DeleteInviteUseCase deleteInviteUseCase;
    private final FindInviteUseCase findInviteUseCase;
    private final CreateGameUseCase createGameUseCase;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketInviteController(CreateInviteUseCase createInviteUseCase, DeleteInviteUseCase deleteInviteUseCase, FindInviteUseCase findInviteUseCase, CreateGameUseCase createGameUseCase, UserRepository userRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.createInviteUseCase = createInviteUseCase;
        this.deleteInviteUseCase = deleteInviteUseCase;
        this.findInviteUseCase = findInviteUseCase;
        this.createGameUseCase = createGameUseCase;
        this.userRepository = userRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/invites/{invitedUuid}/invite")
    public void invite(@DestinationVariable UUID invitedUuid, UUID hostUuid) {
        final var dto = createInviteUseCase.createGameInvite(hostUuid, invitedUuid);
        final var hostName = userRepository.findByUuid(hostUuid).get().username();
        final var invitedName = userRepository.findByUuid(invitedUuid).get().username();
        simpMessagingTemplate.convertAndSend(
                getInviteDestination(invitedUuid), new CreateInviteResponseDto(dto.uuid(), hostName, invitedName, ResponseFor.INVITED));
        simpMessagingTemplate.convertAndSend(
                getInviteDestination(hostUuid), new CreateInviteResponseDto(dto.uuid(), hostName, invitedName, ResponseFor.HOST));
    }

    @MessageMapping("/invites/{inviteUuid}/accept")
    public void accept(@DestinationVariable UUID inviteUuid) {
        final var invite = findInviteUseCase.findInviteByUuid(inviteUuid);

        final var intel = createGameUseCase.createForUserAndUser(
                new CreateForUserAndUserDto(
                        invite.hostPlayerUuid(),
                        invite.invitedPlayerUuid()
                )
        );

        deleteInviteUseCase.deleteInviteByUuid(inviteUuid);
        simpMessagingTemplate.convertAndSend(
                getResponseDestination(inviteUuid),
                new AcceptGameResponseDto(intel, ResponseType.ACCEPTED)
        );
    }

    @MessageMapping("/invites/{inviteUuid}/decline")
    public void decline(@DestinationVariable UUID inviteUuid) {
        deleteInviteUseCase.deleteInviteByUuid(inviteUuid);
        simpMessagingTemplate.convertAndSend(getResponseDestination(inviteUuid),
                new DeclineGameResponseDto(ResponseType.DECLINED));
    }

    private String getInviteDestination(UUID uuid) {
        return "/queue/invite/" + uuid;
    }

    private String getResponseDestination(UUID inviteUuid) {
        return "/topic/invite/" + inviteUuid;
    }
}
