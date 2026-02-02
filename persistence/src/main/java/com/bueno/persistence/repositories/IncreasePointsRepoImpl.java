package com.bueno.persistence.repositories;

import com.bueno.domain.usecases.hand.dtos.IncreasePointsDto;
import com.bueno.domain.usecases.hand.repos.IncreasePointsRepository;
import com.bueno.persistence.ConnectionFactory;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@Repository
public class IncreasePointsRepoImpl implements IncreasePointsRepository {
    @Override
    public void save(IncreasePointsDto dto) {
        String sql = """
            INSERT INTO increase_points (
                uuid, game_uuid, weak_card, medium_card, strong_card, 
                player_type, open_hand, pile, winner_r1, winner_r2, 
                winner_r3, player_points, opponent_points, hand_value, 
                opponent_accepted, general_score_impact
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
        try (PreparedStatement preparedStatement = ConnectionFactory.createPreparedStatement(sql)) {
            var connection = preparedStatement.getConnection();
            java.sql.Array pileArray = connection.createArrayOf("INTEGER", dto.pile().toArray());

            preparedStatement.setObject(1, UUID.randomUUID());
            preparedStatement.setObject(2, dto.gameUuid());
            preparedStatement.setInt(3, dto.weakCardValue());
            preparedStatement.setInt(4, dto.mediumCardValue());
            preparedStatement.setInt(5, dto.strongCardValue());
            preparedStatement.setString(6, dto.playerType());
            preparedStatement.setBoolean(7, dto.openHand());
            preparedStatement.setArray(8, pileArray);
            preparedStatement.setDouble(9, dto.winnerRound1());
            preparedStatement.setDouble(10, dto.winnerRound2());
            preparedStatement.setDouble(11, dto.winnerRound3());
            preparedStatement.setInt(12, dto.playerPoints());
            preparedStatement.setInt(13, dto.opponentPoints());
            preparedStatement.setInt(14, dto.handValue());
            preparedStatement.setInt(15, dto.opponentAccepted());
            preparedStatement.setInt(16, dto.generalScoreImpact());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage() + "| IncreasePoints couldn't be saved");
            e.printStackTrace();
        }
    }
}