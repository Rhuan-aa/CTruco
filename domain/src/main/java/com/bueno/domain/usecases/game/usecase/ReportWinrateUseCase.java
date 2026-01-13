package com.bueno.domain.usecases.game.usecase;

import com.bueno.domain.usecases.game.dtos.PlayerWinrateDto;
import com.bueno.domain.usecases.game.dtos.PlayerWinrateStatsDto;
import com.bueno.domain.usecases.game.repos.GameResultRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportWinrateUseCase {
    private final GameResultRepository gameResultRepository;

    public ReportWinrateUseCase(GameResultRepository gameResultRepository) {
        this.gameResultRepository = gameResultRepository;
    }

    public List<PlayerWinrateDto> createWinrateList() {
        return gameResultRepository.findAll().stream()
                .flatMap(dto -> Stream.of(
                        PlayerWinrateStatsDto.fromGame(dto.p1Name(), dto.winner()),
                        PlayerWinrateStatsDto.fromGame(dto.p2Name(), dto.winner())
                ))
                .collect(Collectors.toMap(
                        PlayerWinrateStatsDto::name,
                        stats -> stats,
                        PlayerWinrateStatsDto::merge
                ))
                .values().stream()
                .map(PlayerWinrateDto::new)
                .sorted(Comparator.comparingDouble(PlayerWinrateDto::winrate).reversed())
                .toList();
    }
}
