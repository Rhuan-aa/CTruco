package com.bueno.persistence.repositories;

import com.bueno.domain.usecases.hand.dtos.MaoDeOnzeDto;
import com.bueno.domain.usecases.hand.repos.MaoDeOnzeRepository;
import com.bueno.persistence.ConnectionFactory;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@Repository
public class MaoDeOnzeRepoImpl implements MaoDeOnzeRepository {
    @Override
    public void save(MaoDeOnzeDto dto) {
        String sql = """
            INSERT INTO mao_de_onze(
                uuid,
                game_uuid,
                weak_card,
                medium_card,
                strong_card,
                player_type,
                player_points,
                opponent_points,
                open_hand,
                hand_winner
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
        try (PreparedStatement preparedStatement = ConnectionFactory.createPreparedStatement(sql)) {
            preparedStatement.setObject(1, UUID.randomUUID());
            preparedStatement.setObject(2, dto.gameUuid());
            preparedStatement.setInt(3, dto.weakCardValue());
            preparedStatement.setInt(4, dto.mediumCardValue());
            preparedStatement.setInt(5, dto.strongCardValue());
            preparedStatement.setString(6, dto.player_type());
            preparedStatement.setInt(7, dto.player_points());
            preparedStatement.setInt(8, dto.opponents_points());
            preparedStatement.setBoolean(9, dto.openHand());
            preparedStatement.setBoolean(10, dto.handWinner());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage() + "| MaoDeOnze couldn't be saved");
            e.printStackTrace();
        }
    }
}
