package com.bueno.domain.usecases.dataset;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.StringJoiner;

@Component
public class DatasetFormatter {

    /**
     * It creates a CSV or TSV formatted String to be provided as a dataset file. This method uses reflection.
     * Any change in DTO attributes will result in a dataset column name change.
     *
     * @param <T>  is an DTO used to generate a dataset.
     * @param data is a list of DTOs of type <T> which is used to create the formatted string.
     * @param type
     * @return CSV or TSV formatted String.
     */
    public <T> String format(List<T> data, ExportFormat type) {
        if (data == null || data.isEmpty()) {
            return "";
        }

        StringBuilder formattedString = new StringBuilder();
        Class<?> reflectedClass = data.get(0).getClass();
        Field[] fields = reflectedClass.getDeclaredFields();
        StringJoiner header = new StringJoiner(type.separator());

        for (Field field : fields) {
            header.add(field.getName());
        }

        formattedString.append(header).append("\n");

        for (T item : data) {
            StringJoiner row = new StringJoiner(type.separator());
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(item);
                    row.add(value != null ? escapeSpecialCharacters(value.toString()) : "");
                } catch (IllegalAccessException e) {
                    row.add("ERROR");
                }
            }
            formattedString.append(row).append("\n");
        }

        return formattedString.toString();
    }

    private String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
