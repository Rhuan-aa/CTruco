package com.bueno.persistence.repositories;

import com.bueno.domain.usecases.invite.dtos.InviteDto;
import com.bueno.domain.usecases.invite.repos.InviteRepository;
import com.bueno.persistence.ConnectionFactory;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InviteRepositoryImpl implements InviteRepository {
    @Override
    public void save(InviteDto inviteDto) {
        String sql = "INSERT INTO invite(uuid, host_player_uuid, invited_player_uuid, expires_at) VALUES (?,?,?,?);";
        try (PreparedStatement preparedStatement = ConnectionFactory.createPreparedStatement(sql)) {
            preparedStatement.setObject(1, inviteDto.uuid());
            preparedStatement.setObject(2, inviteDto.hostPlayerUuid());
            preparedStatement.setObject(3, inviteDto.invitedPlayerUuid());
            preparedStatement.setTimestamp(4, Timestamp.from(inviteDto.expiresAt()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByInviteUuid(UUID inviteUuid) {
        String sql = "DELETE FROM invite WHERE uuid = ?;";
        try (PreparedStatement preparedStatement = ConnectionFactory.createPreparedStatement(sql)) {
            preparedStatement.setObject(1, inviteUuid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<InviteDto> findByUuid(UUID inviteUuid) {
        String sql = "SELECT * FROM invite WHERE uuid = ?;";
        try (PreparedStatement preparedStatement = ConnectionFactory.createPreparedStatement(sql)) {
            preparedStatement.setObject(1, inviteUuid);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return Optional.of(new InviteDto(
                        (UUID) rs.getObject("uuid"),
                        (UUID) rs.getObject("host_player_uuid"),
                        (UUID) rs.getObject("invited_player_uuid"),
                        rs.getTimestamp("expires_at").toInstant()
                ));
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<InviteDto> findByPlayerUuid(UUID playerUuid) {
        String sql = "SELECT * FROM invite WHERE host_player_uuid = ? OR invited_player_uuid = ?;";
        try (PreparedStatement preparedStatement = ConnectionFactory.createPreparedStatement(sql)) {
            preparedStatement.setObject(1, playerUuid);
            preparedStatement.setObject(2, playerUuid);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return Optional.of(new InviteDto(
                        (UUID) rs.getObject("uuid"),
                        (UUID) rs.getObject("host_player_uuid"),
                        (UUID) rs.getObject("invited_player_uuid"),
                        rs.getTimestamp("expires_at").toInstant()
                ));
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
