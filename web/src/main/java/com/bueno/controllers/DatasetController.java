package com.bueno.controllers;

import com.bueno.domain.usecases.dataset.ExportDatasetUseCase;
import com.bueno.domain.usecases.dataset.dto.ExportResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v2/datasets")
public class DatasetController {

    private final ExportDatasetUseCase exportDatasetUsecase;

    public DatasetController(ExportDatasetUseCase exportDatasetUsecase) {
        this.exportDatasetUsecase = exportDatasetUsecase;
    }

    @GetMapping("/played-cards")
    public ResponseEntity<byte[]> downloadAllPlayedCards(
            @RequestParam(defaultValue = "json") String format) {

        ExportResult result = exportDatasetUsecase.exportPlayedCards(format);
        return buildFileResponse(result);
    }

    @GetMapping("/played-cards/{gameUuid}")
    public ResponseEntity<byte[]> downloadPlayedCardsByGame(
            @PathVariable UUID gameUuid,
            @RequestParam(defaultValue = "json") String format) {

        ExportResult result = exportDatasetUsecase.exportPlayedCardsOfGame(gameUuid, format);
        return buildFileResponse(result);
    }

    @GetMapping("/increased-points")
    public ResponseEntity<byte[]> downloadAllIncreasedPoints(
            @RequestParam(defaultValue = "json") String format) {

        ExportResult result = exportDatasetUsecase.exportIncreasedPoints(format);
        return buildFileResponse(result);
    }

    @GetMapping("/increased-points/{gameUuid}")
    public ResponseEntity<byte[]> downloadIncreasedPointsByGame(
            @PathVariable UUID gameUuid,
            @RequestParam(defaultValue = "json") String format) {

        ExportResult result = exportDatasetUsecase.exportIncreasedPointsOfGame(gameUuid, format);
        return buildFileResponse(result);
    }


    @GetMapping("/mao-de-onze")
    public ResponseEntity<byte[]> downloadAllMaoDeOnze(
            @RequestParam(defaultValue = "json") String format) {

        ExportResult result = exportDatasetUsecase.exportMaoDeOnze(format);
        return buildFileResponse(result);
    }

    @GetMapping("/mao-de-onze/{gameUuid}")
    public ResponseEntity<byte[]> downloadMaoDeOnzeByGame(
            @PathVariable UUID gameUuid,
            @RequestParam(defaultValue = "json") String format) {

        ExportResult result = exportDatasetUsecase.exportMaoDeOnzeOfGame(gameUuid, format);
        return buildFileResponse(result);
    }

    private ResponseEntity<byte[]> buildFileResponse(ExportResult result) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.filename() + "\"")
                .contentType(MediaType.parseMediaType(result.contentType()))
                .body(result.content());
    }
}