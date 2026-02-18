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
                    open_hand,
                    deck_pile,
                    round_number,
                    winner_r1,
                    winner_r2,
                    winner_r3,
                    choice,
                    choice_is_closed,
                    hand_winner
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;
        try (PreparedStatement preparedStatement = ConnectionFactory.createPreparedStatement(sql)) {
            preparedStatement.setObject(1, UUID.randomUUID());
            preparedStatement.setObject(2, dto.gameUuid());
            preparedStatement.setInt(3, dto.weakCardValue());
            preparedStatement.setInt(4, dto.mediumCardValue());
            preparedStatement.setInt(5, dto.strongCardValue());
            preparedStatement.setString(6, dto.playerType());
            preparedStatement.setBoolean(7, dto.openHand());
            preparedStatement.setInt(8, dto.deckPile());
            preparedStatement.setInt(9, dto.roundNumber());
            preparedStatement.setDouble(10, dto.winnerR1());
            preparedStatement.setDouble(11, dto.winnerR2());
            preparedStatement.setDouble(12, dto.winnerR3());
            preparedStatement.setInt(13, dto.choice());
            preparedStatement.setInt(14, dto.choiceIsClosed() ? 1 : 0);
            preparedStatement.setBoolean(15, dto.handWinner());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage() + "| PlayedCard couldn't be saved");
            e.printStackTrace();
        }
    }
}