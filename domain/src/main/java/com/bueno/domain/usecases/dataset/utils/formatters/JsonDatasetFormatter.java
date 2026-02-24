package com.bueno.domain.usecases.dataset.utils.formatters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonDatasetFormatter implements DatasetFormatter {

    private final ObjectMapper objectMapper;

    public JsonDatasetFormatter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public <T> String format(List<T> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter para JSON", e);
        }
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public String getExtension() {
        return ".json";
    }

    @Override
    public String getFormatName() {
        return "JSON";
    }
}

