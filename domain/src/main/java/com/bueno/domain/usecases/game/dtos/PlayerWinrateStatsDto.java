package com.bueno.domain.usecases.game.dtos;

public record PlayerWinrateStatsDto(
        String name,
        int wins,
        int totalGames) {
    private static final double BASELINE_WINRATE = 0.5;
    private static final double CONFIDENCE_WEIGHT = 15.0;

    public static PlayerWinrateStatsDto fromGame(String playerName, String winnerName) {
        boolean isWinner = playerName.equals(winnerName);
        return new PlayerWinrateStatsDto(playerName, isWinner ? 1 : 0, 1);
    }

    public PlayerWinrateStatsDto merge(PlayerWinrateStatsDto other){
        return new PlayerWinrateStatsDto(
                name,
                wins + other.wins(),
                totalGames + other.totalGames
        );
    }

    public double getBayesianScore() {
        return (wins + (CONFIDENCE_WEIGHT * BASELINE_WINRATE)) / (totalGames + CONFIDENCE_WEIGHT);
    }

    public boolean isQualified() {
        return totalGames >= 15;
    }
}
