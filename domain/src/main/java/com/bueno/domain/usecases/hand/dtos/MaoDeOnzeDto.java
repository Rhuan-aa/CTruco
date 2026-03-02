package com.bueno.domain.usecases.hand.dtos;

import java.util.UUID;

public record MaoDeOnzeDto (
        UUID gameUuid,
        int weakCardValue,
        int mediumCardValue,
        int strongCardValue,
        String playerType,
        int playerPoints,
        int opponentsPoints,
        boolean openHand,
        boolean handWinner
) { }
