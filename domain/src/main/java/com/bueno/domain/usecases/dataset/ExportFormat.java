package com.bueno.domain.usecases.dataset;

public enum ExportFormat {
    CSV,
    TSV;

    public String separator() {
        return this == ExportFormat.CSV ? "," : "\t";
    }

    public String extension() {
        return this.toString().toLowerCase();
    }
}
