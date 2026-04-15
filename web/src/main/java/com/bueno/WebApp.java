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

package com.bueno;

import com.bueno.domain.usecases.bot.dtos.RemoteBotDto;
import com.bueno.domain.usecases.bot.dtos.TransientRemoteBotDto;
import com.bueno.domain.usecases.bot.repository.RemoteBotRepository;
import com.bueno.domain.usecases.game.dtos.GameResultDto;
import com.bueno.domain.usecases.game.repos.GameResultRepository;
import com.bueno.domain.usecases.game.usecase.RankBotsOnTime;
import com.bueno.domain.usecases.tournament.repos.MatchRepository;
import com.bueno.domain.usecases.tournament.repos.TournamentRepository;
import com.bueno.domain.usecases.user.RegisterUserUseCase;
import com.bueno.domain.usecases.user.UserRepository;
import com.bueno.domain.usecases.user.dtos.ApplicationUserDto;
import com.bueno.domain.usecases.user.dtos.RegisterUserRequestDto;
import com.bueno.persistence.DataBaseBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

@SpringBootApplication
@EnableScheduling
public class WebApp {
    public static void main(String[] args) throws SQLException {
        DataBaseBuilder dataBaseBuilder = new DataBaseBuilder();
        dataBaseBuilder.buildDataBaseIfMissing();
        SpringApplication.run(WebApp.class, args);
    }

    @Bean
    CommandLineRunner run(RegisterUserUseCase registerUserUseCase,
                          GameResultRepository gameResultRepository,
                          PasswordEncoder encoder,
                          RemoteBotRepository botRepository,
                          TournamentRepository tournamentRepository,
                          MatchRepository matchRepository,
                          UserRepository userRepository
    ) {
        return args -> {
            TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
            if (botRepository.findAll().isEmpty()) {
                tournamentRepository.deleteAll();
                matchRepository.deleteAll();
                UUID defaultUuid;
                Optional<ApplicationUserDto> maybeUser = userRepository.findByEmail("lucas.ruas@gmail.com");

                if (maybeUser.isEmpty()) {
                    final String encodedPassword = encoder.encode("123123");
                    final RegisterUserRequestDto defaultUser = new RegisterUserRequestDto("Lucas", encodedPassword, "lucas.ruas@gmail.com");

                    defaultUuid = registerUserUseCase.create(defaultUser).uuid();
                } else {
                    defaultUuid = maybeUser.get().uuid();
                }

                final TransientRemoteBotDto remoteBot = new TransientRemoteBotDto(UUID.randomUUID(), defaultUuid, "Remote Bot", "http://localhost", "8030", "https://github.com/gcontiero11/CTruco");
                botRepository.save(remoteBot);
                botRepository.authorizeByUuid(remoteBot.uuid());
            }
        };
    }
}
