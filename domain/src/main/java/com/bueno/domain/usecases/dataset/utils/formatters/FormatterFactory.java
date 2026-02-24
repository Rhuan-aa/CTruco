package com.bueno.domain.usecases.dataset.utils.formatters;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FormatterFactory {

    private final Map<String, DatasetFormatter> formatters;

    public FormatterFactory(List<DatasetFormatter> formatterList) {
        this.formatters = formatterList
                .stream()
                .collect(
                        Collectors.toMap(f -> f.getFormatName().toUpperCase(), Function.identity())
                );
    }

    public DatasetFormatter getFormatter(String format) {
        DatasetFormatter formatter = formatters.get(format.toUpperCase());
        if (formatter == null) throw new IllegalArgumentException("Formato não suportado: " + format);
        return formatter;
    }
}
