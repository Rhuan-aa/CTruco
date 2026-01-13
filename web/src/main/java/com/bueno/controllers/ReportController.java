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

package com.bueno.controllers;

import com.bueno.domain.usecases.game.dtos.PlayerWinrateDto;
import com.bueno.domain.usecases.game.usecase.ReportTopWinnersUseCase;
import com.bueno.domain.usecases.game.dtos.TopWinnersDto;
import com.bueno.domain.usecases.game.usecase.ReportWinrateUseCase;
import com.bueno.responses.ResponseBuilder;
import com.bueno.responses.ResponseEntry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/reports")
public class ReportController {

    private final ReportWinrateUseCase reportWinrateUseCase;

    public ReportController(ReportWinrateUseCase reportWinrateUseCase) {
        this.reportWinrateUseCase = reportWinrateUseCase;
    }

    @GetMapping(path = "/top-winners/{numberOfTopWinners}")
    private ResponseEntity<?> topWinners(@PathVariable int numberOfTopWinners) {
        try {
            ReportTopWinnersUseCase useCase = new ReportTopWinnersUseCase(reportWinrateUseCase);
            var response = useCase.create(numberOfTopWinners);
            return new ResponseBuilder(HttpStatus.OK)
                    .addEntry(new ResponseEntry("topWinners", response))
                    .addTimestamp()
                    .build();
        } catch (Exception e) {
            return new ResponseBuilder(HttpStatus.NOT_FOUND)
                    .addEntry(new ResponseEntry("error", "the server couldn't found the top winners"))
                    .addTimestamp()
                    .build();
        }
    }

    @GetMapping(path = "/winrate")
    private ResponseEntity<?> winrate() {
        try {
            var response = reportWinrateUseCase.createWinrateList();
            return new ResponseBuilder(HttpStatus.OK)
                    .addEntry(new ResponseEntry("players_winrate", response))
                    .addTimestamp()
                    .build();
        } catch (Exception e) {
            return new ResponseBuilder(HttpStatus.NOT_FOUND)
                    .addEntry(new ResponseEntry("error", "the server couldn't found the winrate"))
                    .addTimestamp()
                    .build();
        }
    }
}
