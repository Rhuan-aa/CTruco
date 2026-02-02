package com.bueno.domain.usecases.hand.repos;

import com.bueno.domain.usecases.hand.dtos.IncreasePointsDto;

public interface IncreasePointsRepository {
    void save(IncreasePointsDto dto);
}
