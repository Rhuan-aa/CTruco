package com.bueno.domain.usecases.session.usecase;

import com.bueno.domain.usecases.game.repos.GameRepository;
import com.bueno.domain.usecases.session.dtos.InvitablePlayerDto;
import com.bueno.domain.usecases.session.dtos.SessionDto;
import com.bueno.domain.usecases.session.repos.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FindSessionUseCase {

    private final SessionRepository sessionRepo;
    private final GameRepository gameRepo;

    @Autowired
    public FindSessionUseCase(SessionRepository sessionRepo, GameRepository gameRepo) {
        this.sessionRepo = sessionRepo;
        this.gameRepo = gameRepo;
    }

    public List<InvitablePlayerDto> findInvitableSessions(UUID playerUuid){
        return sessionRepo
                .findInvitableSessions(playerUuid)
                .stream()
                .filter(dto -> !gameRepo.findByPlayerUuid(dto.playerUuid()).isPresent())
                .toList();
    }

    public SessionDto findSessionByPlayerUuid(UUID playerUuid) {
        Objects.requireNonNull(playerUuid, "Player's uuid must be not null");
        final var dto = sessionRepo.findByPlayerUuid(playerUuid);
        return dto.orElse(null);
    }

    public boolean isPlayerInvitable(UUID hostPlayerUuid, UUID invitedPlayerUuid) {
        final var player = findInvitableSessions(hostPlayerUuid)
                .stream()
                .map(InvitablePlayerDto::playerUuid)
                .anyMatch(uuid -> uuid.equals(invitedPlayerUuid));

        return player || gameRepo.findByPlayerUuid(invitedPlayerUuid).isEmpty();
    }

}
