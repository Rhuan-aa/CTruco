package com.bueno.domain.usecases.hand.repos;

import java.util.List;
import java.util.UUID;

public interface DatasetRepository <T> {
    void save(T data);
    List<T> findAll();
}
