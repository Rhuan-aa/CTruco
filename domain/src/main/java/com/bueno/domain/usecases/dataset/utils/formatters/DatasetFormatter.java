package com.bueno.domain.usecases.dataset.utils.formatters;

import java.util.List;

public interface DatasetFormatter {
    <T> String format(List<T> data);
    String getContentType();
    String getExtension();
    String getFormatName();
}
