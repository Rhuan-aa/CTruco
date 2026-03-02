package com.bueno.controllers;

import com.bueno.domain.usecases.dataset.ExportDatasetUseCase;
import com.bueno.domain.usecases.dataset.ExportResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v2/datasets")
public class DatasetController {

    private final ExportDatasetUseCase exportDatasetUsecase;

    public DatasetController(ExportDatasetUseCase exportDatasetUsecase) {
        this.exportDatasetUsecase = exportDatasetUsecase;
    }

    @GetMapping("/played-cards")
    public ResponseEntity<byte[]> downloadAllPlayedCards(
            @RequestParam(defaultValue = "csv") String format) {

        ExportResult result = exportDatasetUsecase.exportPlayedCards(format);
        return buildFileResponse(result);
    }

    @GetMapping("/increased-points")
    public ResponseEntity<byte[]> downloadAllIncreasedPoints(
            @RequestParam(defaultValue = "csv") String format) {

        ExportResult result = exportDatasetUsecase.exportIncreasedPoints(format);
        return buildFileResponse(result);
    }

    @GetMapping("/mao-de-onze")
    public ResponseEntity<byte[]> downloadAllMaoDeOnze(
            @RequestParam(defaultValue = "csv") String format) {

        ExportResult result = exportDatasetUsecase.exportMaoDeOnze(format);
        return buildFileResponse(result);
    }

    private ResponseEntity<byte[]> buildFileResponse(ExportResult result) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.filename() + "\"")
                .contentType(MediaType.parseMediaType(result.contentType()))
                .body(result.content());
    }
}