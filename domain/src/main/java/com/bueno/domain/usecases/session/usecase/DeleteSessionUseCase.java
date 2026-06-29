package com.bueno.domain.usecases.session.usecase;

import com.bueno.domain.usecases.session.dtos.SessionDto;
import com.bueno.domain.usecases.session.repos.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeleteSessionUseCase {

    private final SessionRepository sessionRepo;

    @Autowired
    public DeleteSessionUseCase(SessionRepository sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    public void deleteByPlayerId(UUID playerUuid) {
        Objects.requireNonNull(playerUuid, "Player UUID cannot be null!");
        Optional<SessionDto> sessionOpt = sessionRepo.findByPlayerUuid(playerUuid);
        sessionOpt.ifPresent(sessionDto ->
                sessionRepo.delete(sessionDto.uuid())
        );
    }

    public void deleteAllExpired() {
        sessionRepo
                .findAllExpired()
                .forEach(sessionDto -> sessionRepo.delete(sessionDto.playerUuid()));
    }
}