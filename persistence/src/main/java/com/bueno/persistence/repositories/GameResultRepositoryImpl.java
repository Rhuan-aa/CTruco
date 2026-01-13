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

package com.bueno.persistence.repositories;

import com.bueno.domain.usecases.game.dtos.GameResultDto;
import com.bueno.domain.usecases.game.dtos.GameResultUsernamesDto;
import com.bueno.domain.usecases.game.dtos.PlayerWinsDto;
import com.bueno.domain.usecases.game.repos.GameResultRepository;
import com.bueno.persistence.ConnectionFactory;
import com.bueno.persistence.dto.GameResultQR;
import com.bueno.persistence.dto.PlayerWinsQR;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class GameResultRepositoryImpl implements GameResultRepository {

    public GameResultRepositoryImpl() {
    }

    @Override
    public void save(GameResultDto gameResult) {
        String sql = """
                INSERT INTO game_result(game_uuid,game_start,game_end,winner_uuid,player1_uuid,player1_score,player2_uuid,player2_score)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """;
        try (PreparedStatement preparedStatement = ConnectionFactory.createPreparedStatement(sql)) {
            preparedStatement.setObject(1, gameResult.gameUuid());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(gameResult.gameStart()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(gameResult.gameEnd()));
            preparedStatement.setObject(4, gameResult.winnerUuid());
            preparedStatement.setObject(5, gameResult.player1Uuid());
            preparedStatement.setInt(6, gameResult.player1Score());
            preparedStatement.setObject(7, gameResult.player2Uuid());
            preparedStatement.setLong(8, gameResult.player2Score());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage() + "| GameResult couldn't be saved");
            e.printStackTrace();
        }
    }

    @Override
    public List<GameResultUsernamesDto> findAll() {
        String sql = """
                SELECT
                       game.game_uuid     AS game_uuid,
                       game.game_end      AS ending,
                       game.game_start    as start,
                       game.player1_score AS player1_score,
                       game.player2_score AS player2_score,
                       player1.username   AS player1,
                       player2.username   AS player2,
                       winner.username    AS winner
                FROM game_result game
                         JOIN
                     app_user player1 ON game.player1_uuid = player1.uuid
                         JOIN
                     app_user player2 ON game.player2_uuid = player2.uuid
                         JOIN
                     app_user winner ON game.winner_uuid = winner.uuid;
                """;
        try (PreparedStatement statement = ConnectionFactory.createPreparedStatement(sql)) {
            List<GameResultQR> gameResultList = new ArrayList<>();
            return getGameResultUsernamesDtos(statement, gameResultList);
        } catch (SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return List.of();
    }

    @Override
    public List<GameResultUsernamesDto> findAllByUserUuid(UUID uuid) {
        String sql = """
                SELECT
                       game.game_uuid     AS game_uuid,
                       game.game_end      AS ending,
                       game.game_start    as start,
                       game.player1_score AS player1_score,
                       game.player2_score AS player2_score,
                       player1.username   AS player1,
                       player2.username   AS player2,
                       winner.username    AS winner
                FROM game_result game
                         JOIN
                     app_user player1 ON game.player1_uuid = player1.uuid
                         JOIN
                     app_user player2 ON game.player2_uuid = player2.uuid
                         JOIN
                     app_user winner ON game.winner_uuid = winner.uuid
                WHERE game.player1_uuid = ?
                   OR game.player2_uuid = ?
                ORDER BY game.game_end DESC;
                """;
        try (PreparedStatement statement = ConnectionFactory.createPreparedStatement(sql)) {
            List<GameResultQR> gameResults = new ArrayList<>();
            statement.setObject(1, uuid);
            statement.setObject(2, uuid);
            return getGameResultUsernamesDtos(statement, gameResults);

        } catch (SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return List.of();

    }

    private List<GameResultUsernamesDto> getGameResultUsernamesDtos(PreparedStatement statement, List<GameResultQR> gameResultList) throws SQLException {
        ResultSet res = statement.executeQuery();
        while (res.next()) gameResultList.add(new GameResultQR(
                res.getObject("game_uuid", UUID.class),
                res.getObject("ending", LocalDateTime.class),
                res.getObject("start", LocalDateTime.class),
                res.getInt("player1_score"),
                res.getInt("player2_score"),
                res.getString("player1"),
                res.getString("player2"),
                res.getString("winner")
        ));
        return gameResultList.stream().map(r -> new GameResultUsernamesDto(
                        r.gameId(),
                        dateTimeFormatter(r.ending().toLocalDate(), r.ending().getHour(), r.ending().getMinute()),
                        dateTimeFormatter(r.start().toLocalDate(), r.start().getHour(), r.start().getMinute()),
                        r.start().until(r.ending(), ChronoUnit.SECONDS),
                        r.p1Score(),
                        r.p2Score(),
                        r.p1Name(),
                        r.p2Name(),
                        r.winner()
                ))
                .toList();
    }

    private String dateTimeFormatter(LocalDate date, long minute, long hour) {
        return date + " " +
                hour +
                ":" +
                (minute > 9 ? minute : ("0" + minute));
    }
}
