package com.bueno.persistence.repositories;

import com.bueno.domain.usecases.hand.dtos.MaoDeOnzeDto;
import com.bueno.domain.usecases.hand.repos.MaoDeOnzeRepository;
import com.bueno.persistence.ConnectionFactory;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
            preparedStatement.setString(6, dto.playerType());
            preparedStatement.setInt(7, dto.playerPoints());
            preparedStatement.setInt(8, dto.opponentsPoints());
            preparedStatement.setBoolean(9, dto.openHand());
            preparedStatement.setBoolean(10, dto.handWinner());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage() + "| MaoDeOnze couldn't be saved");
            e.printStackTrace();
        }
    }

    @Override
    public List<MaoDeOnzeDto> findAll() {
        String sql = "SELECT * FROM mao_de_onze";
        List<MaoDeOnzeDto> results = new ArrayList<>();

        try (PreparedStatement statement = ConnectionFactory.createPreparedStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                results.add(mapResultSetToDto(resultSet));
            }
        } catch (SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage() + "| Could not retrieve all MaoDeOnze records");
            e.printStackTrace();
        }
        return results;
    }

    private MaoDeOnzeDto mapResultSetToDto(ResultSet rs) throws SQLException {
        return new MaoDeOnzeDto(
                UUID.fromString(rs.getString("game_uuid")),
                rs.getInt("weak_card"),
                rs.getInt("medium_card"),
                rs.getInt("strong_card"),
                rs.getString("playerType"),
                rs.getInt("playerPoints"),
                rs.getInt("opponent_points"),
                rs.getBoolean("open_hand"),
                rs.getBoolean("hand_winner")
        );
    }
}