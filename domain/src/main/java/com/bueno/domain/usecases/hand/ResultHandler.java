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
import com.bueno.domain.entities.hand.Hand;
import com.bueno.domain.usecases.hand.converter.HandResultConverter;
import com.bueno.domain.usecases.hand.converter.IncreasedPointsConverter;
import com.bueno.domain.usecases.hand.converter.MaoDeOnzeConverter;
import com.bueno.domain.usecases.hand.converter.PlayedCardConverter;
import com.bueno.domain.usecases.hand.repos.HandResultRepository;
import com.bueno.domain.usecases.hand.repos.IncreasedPointsRepository;
import com.bueno.domain.usecases.hand.repos.MaoDeOnzeRepository;
import com.bueno.domain.usecases.hand.repos.PlayedCardRepository;
import com.bueno.domain.usecases.intel.converters.IntelConverter;
import com.bueno.domain.usecases.intel.dtos.IntelDto;

class ResultHandler {

    private final HandResultRepository handResultRepository;
    private final MaoDeOnzeRepository maoDeOnzeRepository;
    private final IncreasedPointsRepository increasePointsRepository;
    private final PlayedCardRepository playedCardRepository;

    ResultHandler(HandResultRepository handResultRepository, MaoDeOnzeRepository maoDeOnzeRepository, IncreasedPointsRepository increasePointsRepository, PlayedCardRepository playedCardRepository) {
        this.maoDeOnzeRepository = maoDeOnzeRepository;
        this.handResultRepository = handResultRepository;
        this.increasePointsRepository = increasePointsRepository;
        this.playedCardRepository = playedCardRepository;
    }

    IntelDto handle(Game game) {
        Hand hand = game.currentHand();
        hand.getResult().ifPresent(unused -> {
            if (handResultRepository != null) {
                handResultRepository.save(HandResultConverter.of(game));

                if (shouldSaveMaoDeOnze(hand)) {
                    maoDeOnzeRepository.save(MaoDeOnzeConverter.of(game));
                }
            }

            if (increasePointsRepository != null && shouldSaveIncreasedPoints(hand)) {
                IncreasedPointsConverter.of(game).forEach(increasePointsRepository::save);
            }

            if (playedCardRepository != null) {
                PlayedCardConverter.of(game).forEach(playedCardRepository::save);
            }

            updateGameStatus(game);
        });

        if (game.isDone()) {
            return IntelConverter.toDto(game.getIntel());
        }
        return null;
    }

    private boolean shouldSaveIncreasedPoints(Hand hand) {
        return hand.getLastBetRaiser() != null;
    }

    private boolean shouldSaveMaoDeOnze(Hand hand) {
        return hand.isMaoDeOnze() &&
                hand.getLastIntel()
                        .event()
                        .map(e -> !e.equals("QUIT_HAND"))
                        .orElse(false);
    }

    private void updateGameStatus(Game game) {
        game.updateScores();
        if (!game.isDone()) game.prepareNewHand();
    }
}
