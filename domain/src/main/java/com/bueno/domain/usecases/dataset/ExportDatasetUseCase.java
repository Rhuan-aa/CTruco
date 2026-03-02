package com.bueno.domain.usecases.dataset;

import com.bueno.domain.usecases.hand.repos.IncreasedPointsRepository;
import com.bueno.domain.usecases.hand.repos.MaoDeOnzeRepository;
import com.bueno.domain.usecases.hand.repos.PlayedCardRepository;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Service
public class ExportDatasetUseCase {

    private final PlayedCardRepository playedCardRepository;
    private final IncreasedPointsRepository increasedPointsRepository;
    private final MaoDeOnzeRepository maoDeOnzeRepository;

    public ExportDatasetUseCase(PlayedCardRepository playedCardRepository, IncreasedPointsRepository increasedPointsRepository, MaoDeOnzeRepository maoDeOnzeRepository) {
        this.playedCardRepository = playedCardRepository;
        this.increasedPointsRepository = increasedPointsRepository;
        this.maoDeOnzeRepository = maoDeOnzeRepository;
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
        DatasetFormatter formatter = new DatasetFormatter();
        ExportFormat exportFormat = ExportFormat.valueOf(format.toUpperCase());

        String contentString = formatter.format(data, exportFormat);
        byte[] bytes = contentString.getBytes(StandardCharsets.UTF_8);

        String filename = baseFilename + "_" + System.currentTimeMillis() + "." + exportFormat.extension();

        return new ExportResult(filename, bytes, "text/" + exportFormat.extension());
    }
}
