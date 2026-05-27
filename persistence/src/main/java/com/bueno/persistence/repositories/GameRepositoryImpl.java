/*
 *  Copyright (C) 2022 Lucas B. R. de Oliveira - IFSP/SCL
 *  Contact: lucas <dot> oliveira <at> ifsp <dot> edu <dot> br
 *
 *  This file is part of CTruco (Truco game for didactic purpose).
 *
 *  CTruco is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CTruco is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CTruco.  If not, see <https://www.gnu.org/licenses/>
 */

package com.bueno.persistence.repositories;

import com.bueno.domain.usecases.game.dtos.GameDto;
import com.bueno.domain.usecases.game.dtos.PlayerDto;
import com.bueno.domain.usecases.game.repos.GameRepository;
import com.bueno.domain.usecases.hand.dtos.HandDto;
import com.bueno.domain.usecases.intel.dtos.IntelDto;
import com.bueno.domain.usecases.utils.exceptions.EntityNotFoundException;
import com.bueno.persistence.dao.GameDao;
import com.bueno.persistence.dao.PlayerDao;
import com.bueno.persistence.dto.GameEntity;
import com.bueno.persistence.dto.PlayerEntity;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Repository
public class GameRepositoryImpl implements GameRepository {

    private final GameDao gameDao;
    private final PlayerDao playerDao;

    public GameRepositoryImpl(GameDao dao, PlayerDao playerDao) {
        this.gameDao = dao;
        this.playerDao = playerDao;
    }

    @Override
    public void save(GameDto dto) {
        gameDao.findById(dto.gameUuid())
                .ifPresent(game -> {throw new EntityNotFoundException("Game already exists: " + game.getId());});
        playerDao.save(PlayerEntity.from(dto.player1()));
        playerDao.save(PlayerEntity.from(dto.player2()));
        gameDao.save(GameEntity.from(dto));
    }

    @Override
    public void update(GameDto dto) {
        gameDao.findById(dto.gameUuid())
                .orElseThrow(() -> new EntityNotFoundException("Can not update non-existing game: " + dto.gameUuid()));
        playerDao.save(PlayerEntity.from(dto.player1()));
        playerDao.save(PlayerEntity.from(dto.player2()));
        gameDao.save(GameEntity.from(dto));
    }

    @Override
    public void delete(UUID uuid) {
        final GameEntity game = gameDao.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Can not update non-existing game: " + uuid));
        playerDao.deleteById(game.getPlayer1());
        playerDao.deleteById(game.getPlayer2());
        gameDao.delete(game);
    }

    @Override
    public Optional<GameDto> findByPlayerUuid(UUID playerUuid) {
        final UUID uuid = Objects.requireNonNull(playerUuid, "User UUID must not be null.");
        final Optional<GameEntity> possibleGame = gameDao.findByPlayer1OrPlayer2(uuid, uuid);
        return getGameDto(possibleGame.orElse(null));
    }

    @Override
    public Collection<GameDto> findAllInactiveAfter(int minutes) {
        return gameDao.findAll().stream()
                .filter(game -> isInactive(game, minutes))
                .map(this::getGameDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public boolean isInactive(GameEntity game, int minutes){
        try {
            if(game.getHands() == null || game.getHands().isEmpty()) return false;
            final int index = game.getHands().size() - 1;
            Map<UUID, PlayerDto> players = playersMap(game);
            if (players.isEmpty()) return false;
            final HandDto handDto = game.getHands().get(index).toDto(game.getId(), players);
            if (handDto.history() == null || handDto.history().isEmpty()) return false;
            final IntelDto intel = handDto.history().get(handDto.history().size() - 1);
            final Instant lastInteraction = intel.timestamp();
            final Instant now = Instant.now();
            final long inactivityInMinutes = Duration.between(lastInteraction, now).toMinutes();
            return inactivityInMinutes >= minutes;
        } catch (Exception e) {
            return false;
        }
    }

    private Map<UUID, PlayerDto> playersMap(GameEntity game) {
        try {
            Optional<PlayerEntity> p1 = playerDao.findById(game.getPlayer1());
            Optional<PlayerEntity> p2 = playerDao.findById(game.getPlayer2());
            if (p1.isPresent() && p2.isPresent()) {
                Map<UUID, PlayerDto> players = new HashMap<>();
                players.put(p1.get().getId(), p1.get().toDto());
                players.put(p2.get().getId(), p2.get().toDto());
                return players;
            }
        } catch (Exception e) {}
        return Collections.emptyMap();
    }

    private Optional<GameDto> getGameDto(GameEntity game) {
        if(game == null) return Optional.empty();
        try {
            Map<UUID, PlayerDto> players = playersMap(game);
            if (players.size() < 2) return Optional.empty();
            return Optional.of(game.toDto(players));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
