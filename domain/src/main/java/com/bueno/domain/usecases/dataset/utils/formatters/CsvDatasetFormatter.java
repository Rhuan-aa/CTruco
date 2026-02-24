package com.bueno.domain.usecases.dataset.utils.formatters;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.StringJoiner;

@Component
public class CsvDatasetFormatter implements DatasetFormatter {

    @Override
    public <T> String format(List<T> data) {
        if (data == null || data.isEmpty()) {
            return "";
        }

        StringBuilder csv = new StringBuilder();
        Class<?> reflectedClass = data.get(0).getClass();
        Field[] fields = reflectedClass.getDeclaredFields();
        StringJoiner header = new StringJoiner(",");

        for (Field field : fields) {
            header.add(field.getName());
        }

        csv.append(header).append("\n");

        for (T item : data) {
            StringJoiner row = new StringJoiner(",");
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(item);
                    row.add(value != null ? escapeSpecialCharacters(value.toString()) : "");
                } catch (IllegalAccessException e) {
                    row.add("ERROR");
                }
            }
            csv.append(row).append("\n");
        }

        return csv.toString();
    }

    private String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    @Override
    public String getContentType() {
        return "text/csv";
    }

    @Override
    public String getExtension() {
        return ".csv";
    }

    @Override
    public String getFormatName() {
        return "CSV";
    }
}
