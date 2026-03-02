package com.bueno.domain.usecases.dataset;

public record ExportResult(String filename, byte[] content, String contentType) {}
