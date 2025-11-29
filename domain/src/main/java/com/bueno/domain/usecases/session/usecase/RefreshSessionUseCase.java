package com.bueno.domain.usecases.session.usecase;

import com.bueno.domain.entities.session.Session;
import com.bueno.domain.usecases.session.dtos.SessionDto;
import com.bueno.domain.usecases.session.repos.SessionRepository;
import com.bueno.domain.usecases.utils.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class RefreshSessionUseCase {

    private final SessionRepository sessionRepo;

    @Autowired
    public RefreshSessionUseCase(SessionRepository sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    public SessionDto refreshSession(UUID playerUuid) {
        Objects.requireNonNull(playerUuid, "Request model cannot be null!");
        SessionDto newDto = reloadExpiration(playerUuid);
        sessionRepo.update(newDto);
        return newDto;
    }

    private SessionDto reloadExpiration(UUID playerUuid) {
        SessionDto oldDto = sessionRepo.findByPlayerUuid(playerUuid)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Session to refresh not found for player: " + playerUuid));
        Session session = Session.fromDto(oldDto);
        session.reloadExpiration();
        return Session.toDto(session);
    }
}