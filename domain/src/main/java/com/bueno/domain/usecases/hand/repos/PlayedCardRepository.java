package com.bueno.domain.usecases.hand.repos;

import com.bueno.domain.usecases.hand.dtos.PlayedCardDto;

public interface PlayedCardRepository {
    void save(PlayedCardDto dto);
}
