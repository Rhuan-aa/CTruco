package com.bueno.persistence.repositories;

import com.bueno.domain.usecases.session.dtos.InvitablePlayerDto;
import com.bueno.domain.usecases.session.dtos.SessionDto;
import com.bueno.domain.usecases.session.repos.SessionRepository;
import com.bueno.persistence.ConnectionFactory;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Repository
public class SessionRepositoryImpl implements SessionRepository {

    @Override
    public void save(SessionDto sessionDto) {
        String sql = "INSERT INTO session(uuid, player_uuid, expires_at) VALUES (?,?,?)";
        try (PreparedStatement preparedStatement = ConnectionFactory.createPreparedStatement(sql)) {
            preparedStatement.setObject(1, sessionDto.uuid());
            preparedStatement.setObject(2, sessionDto.playerUuid());
            preparedStatement.setTimestamp(3, Timestamp.from(sessionDto.expiresAt()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(SessionDto sessionDto) {
        String sql = "UPDATE session SET player_uuid = ?, expires_at = ? WHERE uuid = ?;";
        try (PreparedStatement preparedStatement = ConnectionFactory.createPreparedStatement(sql)) {
            preparedStatement.setObject(1, sessionDto.playerUuid());
            preparedStatement.setTimestamp(2, Timestamp.from(sessionDto.expiresAt()));
            preparedStatement.setObject(3, sessionDto.uuid());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UUID playerUuid) {
        String sql = "DELETE FROM session WHERE player_uuid = ?";
        try (PreparedStatement preparedStatement = ConnectionFactory.createPreparedStatement(sql)) {
            preparedStatement.setObject(1, playerUuid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SessionDto> findByPlayerUuid(UUID playerUuid) {
        String sql = "SELECT * FROM session WHERE player_uuid = ?";
        try (PreparedStatement preparedStatement = ConnectionFactory.createPreparedStatement(sql)) {
            preparedStatement.setObject(1, playerUuid);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return Optional.of(new SessionDto(
                        rs.getObject("uuid", UUID.class),
                        rs.getObject("player_uuid", UUID.class),
                        rs.getTimestamp(3).toInstant())
                );
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<InvitablePlayerDto> findInvitableSessions(UUID playerUuid) {
        String sql = """
                SELECT
                    player.uuid AS player_uuid,
                    player.username AS player_username,
                    (SELECT COUNT(*)
                     FROM game_result gr
                     WHERE gr.winner_uuid = player.uuid) AS player_score
                FROM
                    app_user player
                INNER JOIN
                    session ON player.uuid = session.player_uuid AND session.expires_at >= ?
                WHERE
                    NOT EXISTS (SELECT 1 FROM invite WHERE invite.host_player_uuid = player.uuid)
                    AND NOT EXISTS (SELECT 1 FROM invite WHERE invite.invited_player_uuid = player.uuid)
                    AND player.uuid != ?;
                    """;

        try (PreparedStatement preparedStatement = ConnectionFactory.createPreparedStatement(sql)) {
            preparedStatement.setObject(1, Timestamp.from(Instant.now()));
            preparedStatement.setObject(2, playerUuid);
            ResultSet rs = preparedStatement.executeQuery();
            List<InvitablePlayerDto> invitablePlayers = new ArrayList<>();

            while (rs.next()) {
                invitablePlayers
                        .add(new InvitablePlayerDto(
                                (UUID) rs.getObject("player_uuid"),
                                rs.getString("player_username"),
                                rs.getInt("player_score"))
                        );
            }

            return invitablePlayers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<SessionDto> findAllExpired() {
        String sql = "SELECT * FROM session WHERE expires_at = ?";

        try (PreparedStatement preparedStatement = ConnectionFactory.createPreparedStatement(sql)) {
            preparedStatement.setObject(1, Timestamp.from(Instant.now()));
            return getSessionDtos(preparedStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<SessionDto> getSessionDtos(PreparedStatement preparedStatement) throws SQLException {
        ResultSet rs = preparedStatement.executeQuery();
        List<SessionDto> sessions = new ArrayList<>();

        while (rs.next()) {
            sessions
                    .add(new SessionDto(
                            (UUID) rs.getObject("uuid"),
                            (UUID) rs.getObject("player_uuid"),
                            Instant.parse(rs.getTimestamp("expires_At").toString()))
                    );
        }

        return sessions;
    }
}
