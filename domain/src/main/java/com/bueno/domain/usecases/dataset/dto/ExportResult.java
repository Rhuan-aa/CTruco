package com.bueno.domain.usecases.dataset.dto;

public record ExportResult(String filename, byte[] content, String contentType) {}
