package com.bueno.domain.usecases.hand.dtos;

import java.util.UUID;

public record PlayedCardDto(
        UUID gameUuid,
        int weakCardValue,
        int mediumCardValue,
        int strongCardValue,
        String playerType,
        boolean openHand,
        int deckPile,
        int roundNumber,
        double winnerR1,
        double winnerR2,
        double winnerR3,
        boolean handWinner,
        int choice,
        boolean choiceIsClosed
) {
}