package com.bueno.domain.usecases.hand.dtos;

public record MaoDeOnzeDto (
        int weakCardValue,
        int mediumCardValue,
        int strongCardValue,
        String player_type,
        int player_points,
        int opponents_points,
        boolean openHand,
        boolean handWinner
) { }
