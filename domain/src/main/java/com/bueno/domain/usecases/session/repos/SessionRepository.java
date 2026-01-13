package com.bueno.domain.usecases.session.repos;

import com.bueno.domain.usecases.session.dtos.InvitablePlayerDto;
import com.bueno.domain.usecases.session.dtos.SessionDto;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository {
    void save(SessionDto sessionDto);
    void update(SessionDto sessionDto);
    void delete(UUID playerUuid);
    Optional<SessionDto> findByPlayerUuid(UUID playerUuid);

    Collection<InvitablePlayerDto> findInvitableSessions(UUID playerUuid);
    Collection<SessionDto> findAllSessions();
    Collection<SessionDto> findAllExpired();
}
