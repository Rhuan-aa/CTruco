package com.bueno.domain.usecases.dataset;

import com.bueno.domain.usecases.dataset.dto.ExportResult;
import com.bueno.domain.usecases.dataset.utils.formatters.DatasetFormatter;
import com.bueno.domain.usecases.dataset.utils.formatters.FormatterFactory;
import com.bueno.domain.usecases.hand.repos.IncreasedPointsRepository;
import com.bueno.domain.usecases.hand.repos.MaoDeOnzeRepository;
import com.bueno.domain.usecases.hand.repos.PlayedCardRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class ExportDatasetUseCase {

    private final PlayedCardRepository playedCardRepository;
    private final IncreasedPointsRepository increasedPointsRepository;
    private final MaoDeOnzeRepository maoDeOnzeRepository;
    private final FormatterFactory formatterFactory;

    public ExportDatasetUseCase(PlayedCardRepository playedCardRepository, IncreasedPointsRepository increasedPointsRepository, MaoDeOnzeRepository maoDeOnzeRepository, FormatterFactory formatterFactory) {
        this.playedCardRepository = playedCardRepository;
        this.increasedPointsRepository = increasedPointsRepository;
        this.maoDeOnzeRepository = maoDeOnzeRepository;
        this.formatterFactory = formatterFactory;
    }

    public ExportResult exportPlayedCardsOfGame(UUID gameUuid, String format) {
        var data = playedCardRepository.findByGameUuid(gameUuid);
        return processExport("played_cards".concat(gameUuid.toString()), format, data);
    }

    public ExportResult exportIncreasedPointsOfGame(UUID gameUuid, String format) {
        var data = increasedPointsRepository.findByGameUuid(gameUuid);
        return processExport("increased_points".concat(gameUuid.toString()), format, data);
    }

    public ExportResult exportMaoDeOnzeOfGame(UUID gameUuid, String format) {
        var data = maoDeOnzeRepository.findByGameUuid(gameUuid);
        return processExport("mao_de_onze".concat(gameUuid.toString()), format, data);
    }

    public ExportResult exportPlayedCards(String format) {
        var data = playedCardRepository.findAll();
        return processExport("played_cards", format, data);
    }

    public ExportResult exportIncreasedPoints(String format) {
        var data = increasedPointsRepository.findAll();
        return processExport("increased_points", format, data);
    }

    public ExportResult exportMaoDeOnze(String format) {
        var data = maoDeOnzeRepository.findAll();
        return processExport("mao_de_onze", format, data);
    }

    private <T> ExportResult processExport(String baseFilename, String format, java.util.List<T> data) {
        DatasetFormatter formatter = formatterFactory.getFormatter(format);

        String contentString = formatter.format(data);
        byte[] bytes = contentString.getBytes(StandardCharsets.UTF_8);

        String filename = baseFilename + "_" + System.currentTimeMillis() + formatter.getExtension();

        return new ExportResult(filename, bytes, formatter.getContentType());
    }
}
