package com.bueno.domain.usecases.hand.dtos;

import java.util.List;
import java.util.UUID;

public record IncreasePointsDto(
        UUID gameUuid,
        int weakCardValue,
        int mediumCardValue,
        int strongCardValue,
        String playerType,
        boolean openHand,
        List<Integer> pile,
        double winnerRound1,
        double winnerRound2,
        double winnerRound3,
        int playerPoints,
        int opponentPoints,
        int handValue,
        int opponentAccepted,
        int generalScoreImpact
) { }