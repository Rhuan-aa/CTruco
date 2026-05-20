package com.bueno.domain.usecases.session.usecase;

import com.bueno.domain.entities.session.Session;
import com.bueno.domain.usecases.session.repos.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class CreateSessionUseCase {

    private final SessionRepository sessionRepo;

    @Autowired
    public CreateSessionUseCase(SessionRepository sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    public void createSessionForUser(UUID playerUuid) {
        Objects.requireNonNull(playerUuid, "Player uuid can not be null!");
        create(playerUuid);
    }

    private void create(UUID playerUuid){
        Session session = Session.of(playerUuid);
        var dto = Session.toDto(session);
        sessionRepo.save(dto);
    }
}
