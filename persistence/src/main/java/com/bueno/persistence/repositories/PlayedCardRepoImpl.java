package com.bueno.persistence.repositories;

import com.bueno.domain.usecases.hand.dtos.PlayedCardDto;
import com.bueno.domain.usecases.hand.repos.PlayedCardRepository;
import com.bueno.persistence.ConnectionFactory;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@Repository
public class PlayedCardRepoImpl implements PlayedCardRepository{

    public void save(PlayedCardDto dto) {
        String sql = """
                INSERT INTO played_card (
                    uuid,
                    game_uuid,
                    weak_card,
                    medium_card,
                    strong_card,
                    player_type,
                    open_card,
                    deck_pile,
                    round_number,
                    winner_r1,
                    winner_r2,
                    winner_r3,
                    hand_winner,
                    choice
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;
        try (PreparedStatement preparedStatement = ConnectionFactory.createPreparedStatement(sql)) {
            preparedStatement.setObject(1, UUID.randomUUID());
            preparedStatement.setObject(2, dto.gameUuid());
            preparedStatement.setInt(3, dto.weakCardValue());
            preparedStatement.setInt(4, dto.mediumCardValue());
            preparedStatement.setInt(5, dto.strongCardValue());
            preparedStatement.setString(6, dto.playerType());
            preparedStatement.setBoolean(7, dto.openCard());
            preparedStatement.setInt(8, dto.deckPile());
            preparedStatement.setInt(9, dto.roundNumber());
            preparedStatement.setDouble(10, dto.winnerR1());
            preparedStatement.setDouble(11, dto.winnerR2());
            preparedStatement.setDouble(12, dto.winnerR3());
            preparedStatement.setBoolean(13, dto.handWinner());
            preparedStatement.setString(14, dto.choice());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage() + "| PlayedCard couldn't be saved");
            e.printStackTrace();
        }
    }
}
