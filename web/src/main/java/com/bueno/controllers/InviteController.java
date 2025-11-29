package com.bueno.controllers;

import com.bueno.domain.usecases.session.usecase.FindSessionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v2/invites/")
public class InviteController {

    final FindSessionUseCase findSessionUseCase;

    public InviteController(FindSessionUseCase findSessionUseCase) {
        this.findSessionUseCase = findSessionUseCase;
    }

    @GetMapping(path = "/online-players/{uuid}")
    public ResponseEntity<?> getOnlinePlayers(@PathVariable UUID uuid){
        final var invitablePlayers = findSessionUseCase.findInvitableSessions(uuid);
        return ResponseEntity.ok(invitablePlayers);
    }
}
