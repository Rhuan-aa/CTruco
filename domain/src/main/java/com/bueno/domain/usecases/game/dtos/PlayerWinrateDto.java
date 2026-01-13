package com.bueno.domain.usecases.game.dtos;

public record PlayerWinrateDto(String name, double winrate, int numberOfMatches, int wins) {
    public PlayerWinrateDto(PlayerWinrateStatsDto stats) {
        this(stats.name(), stats.getBayesianScore(), stats.totalGames(),stats.wins());
    }
}
