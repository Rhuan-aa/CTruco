/*
 *  Copyright (C) 2022 Lucas B. R. de Oliveira - IFSP/SCL
 *  Contact: lucas <dot> oliveira <at> ifsp <dot> edu <dot> br
 *
 *  This file is part of CTruco (Truco game for didactic purpose).
 *
 *  CTruco is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CTruco is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CTruco.  If not, see <https://www.gnu.org/licenses/>
 */

package com.bueno.domain.usecases.game.usecase;

import com.bueno.domain.usecases.game.dtos.PlayerWinrateDto;
import com.bueno.domain.usecases.game.dtos.PlayerWinsDto;
import com.bueno.domain.usecases.game.dtos.TopWinnersDto;
import com.bueno.domain.usecases.game.repos.GameResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportTopWinnersUseCase {

    private final ReportWinrateUseCase reportWinrateUseCase;

    public ReportTopWinnersUseCase(ReportWinrateUseCase reportWinrateUseCase) {
        this.reportWinrateUseCase = reportWinrateUseCase;
    }

    public List<PlayerWinrateDto> create(int numberOfTopPlayers){
        return reportWinrateUseCase.createWinrateList().stream().limit(numberOfTopPlayers).toList();
    }
}
