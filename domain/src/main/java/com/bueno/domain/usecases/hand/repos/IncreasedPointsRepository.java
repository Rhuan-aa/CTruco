package com.bueno.domain.usecases.hand.repos;

import com.bueno.domain.usecases.hand.dtos.IncreasedPointsDto;

public interface IncreasedPointsRepository {
    void save(IncreasedPointsDto dto);
}
