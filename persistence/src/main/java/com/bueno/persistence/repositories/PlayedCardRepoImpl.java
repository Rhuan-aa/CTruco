package com.bueno.persistence.repositories;

import com.bueno.domain.usecases.hand.dtos.PlayedCardDto;
import com.bueno.domain.usecases.hand.repos.PlayedCardRepository;
import com.bueno.persistence.ConnectionFactory;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class PlayedCardRepoImpl implements PlayedCardRepository {
    @Override
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

    @Override
    public List<PlayedCardDto> findAll() {
        String sql = "SELECT * FROM played_card";
        List<PlayedCardDto> results = new ArrayList<>();

        try (PreparedStatement statement = ConnectionFactory.createPreparedStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                results.add(mapResultSetToDto(resultSet));
            }
        } catch (SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage() + "| Could not retrieve all PlayedCard records");
            e.printStackTrace();
        }
        return results;
    }

    private PlayedCardDto mapResultSetToDto(ResultSet rs) throws SQLException {
        return new PlayedCardDto(
                UUID.fromString(rs.getString("game_uuid")),
                rs.getInt("weak_card"),
                rs.getInt("medium_card"),
                rs.getInt("strong_card"),
                rs.getString("playerType"),
                rs.getBoolean("open_hand"),
                rs.getInt("deck_pile"),
                rs.getInt("round_number"),
                rs.getDouble("winner_r1"),
                rs.getDouble("winner_r2"),
                rs.getDouble("winner_r3"),
                rs.getBoolean("hand_winner"),
                rs.getInt("choice"),
                rs.getInt("choice_is_closed") == 1
        );
    }
}