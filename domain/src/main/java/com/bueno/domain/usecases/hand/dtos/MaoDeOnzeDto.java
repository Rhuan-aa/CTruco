package com.bueno.domain.usecases.hand.dtos;

import java.util.UUID;

public record MaoDeOnzeDto (
        UUID gameUuid,
        int weakCardValue,
        int mediumCardValue,
        int strongCardValue,
        String player_type,
        int player_points,
        int opponents_points,
        boolean openHand,
        boolean handWinner
) { }
