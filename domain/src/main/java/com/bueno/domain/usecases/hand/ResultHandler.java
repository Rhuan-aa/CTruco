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

package com.bueno.domain.usecases.hand;

import com.bueno.domain.entities.game.Game;
import com.bueno.domain.usecases.hand.converter.HandResultConverter;
import com.bueno.domain.usecases.hand.converter.IncreasePointsConverter;
import com.bueno.domain.usecases.hand.converter.MaoDeOnzeConverter;
import com.bueno.domain.usecases.hand.repos.HandResultRepository;
import com.bueno.domain.usecases.hand.repos.IncreasePointsRepository;
import com.bueno.domain.usecases.hand.repos.MaoDeOnzeRepository;
import com.bueno.domain.usecases.intel.converters.IntelConverter;
import com.bueno.domain.usecases.intel.dtos.IntelDto;

class ResultHandler {

    private final HandResultRepository handResultRepository;
    private final MaoDeOnzeRepository maoDeOnzeRepository;
    private final IncreasePointsRepository increasePointsRepository;

    ResultHandler(HandResultRepository handResultRepository, MaoDeOnzeRepository maoDeOnzeRepository, IncreasePointsRepository increasePointsRepository) {
        this.maoDeOnzeRepository = maoDeOnzeRepository;
        this.handResultRepository = handResultRepository;
        this.increasePointsRepository = increasePointsRepository;
    }

    IntelDto handle(Game game) {
        game.currentHand().getResult().ifPresent(unused -> {
            if (handResultRepository != null) {
                handResultRepository.save(HandResultConverter.of(game));

                if (game.currentHand().isMaoDeOnze()) {
                    final var dto = MaoDeOnzeConverter.of(game);
                    maoDeOnzeRepository.save(dto);
                }
            }

            if (increasePointsRepository != null) {
                IncreasePointsConverter.ofHand(game.currentHand(), game).forEach(increasePointsRepository::save);
            }

            updateGameStatus(game);
        });

        if (game.isDone()) {
            return IntelConverter.toDto(game.getIntel());
        }
        return null;
    }

    private void updateGameStatus(Game game) {
        game.updateScores();
        if (!game.isDone()) game.prepareNewHand();
    }
}
