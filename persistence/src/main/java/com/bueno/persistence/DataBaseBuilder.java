package com.bueno.persistence;

import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseBuilder {

    public void buildDataBaseIfMissing() throws SQLException {
        System.out.println("Building tables if they don't exists: \n");
        dropDatabases();

        try (Statement statement = ConnectionFactory.createStatement()) {
            statement.addBatch(createAppUserTable());
            statement.addBatch(createRemoteBotsTable());
            statement.addBatch(createGameResultTable());
            statement.addBatch(createHandResultsTable());
            statement.addBatch(createRankBotsTable());
            statement.addBatch(createSessionTable());
            statement.addBatch(createInviteTable());
            statement.addBatch(createMaoDeOnzeTable());
            statement.addBatch(createPlayedCardTable());
            statement.addBatch(createIncreasedPointsTable());
//            statement.addBatch(createTournamentTable());
//            statement.addBatch(createTournamentParticipantsTable());
//            statement.addBatch(createTournamentMatchesTable());
//            statement.addBatch(createTournamentMatchTable());
            statement.executeBatch();

            System.out.println("DATABASE CREATED");
        } catch (SQLException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void dropDatabases() throws SQLException {
        try (Statement statement = ConnectionFactory.createStatement()) {
            statement.addBatch("DROP TABLE IF EXISTS increase_points CASCADE");
            statement.addBatch("DROP TABLE IF EXISTS increased_points CASCADE");
            statement.addBatch("DROP TABLE IF EXISTS mao_de_onze CASCADE");
            statement.addBatch("DROP TABLE IF EXISTS played_card CASCADE");
            statement.addBatch("DROP TABLE IF EXISTS hand_result CASCADE");
            statement.addBatch("DROP TABLE IF EXISTS game_result CASCADE");
            statement.addBatch("DROP TABLE IF EXISTS remote_bot CASCADE");
            statement.addBatch("DROP TABLE IF EXISTS session CASCADE");
            statement.addBatch("DROP TABLE IF EXISTS invite CASCADE");
            statement.addBatch("DROP TABLE IF EXISTS app_user CASCADE");
//            statement.addBatch("DROP TABLE IF EXISTS tournament");
//            statement.addBatch("DROP TABLE IF EXISTS tournament_participant");
//            statement.addBatch("DROP TABLE IF EXISTS tournament_match");
//            statement.addBatch("DROP TABLE IF EXISTS matches");
//            statement.addBatch("DROP TABLE IF EXISTS bot_rank");
            statement.executeBatch();
        }
    }

    private String createAppUserTable() {
        return """
                CREATE TABLE IF NOT EXISTS APP_USER(
                    uuid UUID NOT NULL,
                    username TEXT NOT NULL,
                    email TEXT NOT NULL,
                    password TEXT NOT NULL,
                    CONSTRAINT user_uuid_pk PRIMARY KEY (uuid),
                    CONSTRAINT username_uk UNIQUE (username),
                    CONSTRAINT email_uk UNIQUE (email)
                );
                """;
    }

    private String createRemoteBotsTable() {
        return """
                CREATE TABLE IF NOT EXISTS REMOTE_BOT (
                    uuid UUID NOT NULL,
                    user_uuid UUID NOT NULL,
                    name TEXT NOT NULL,
                    url TEXT NOT NULL,
                    port TEXT NOT NULL,
                    repository_url TEXT NOT NULL,
                    authorized BOOLEAN DEFAULT FALSE,
                    CONSTRAINT remote_bot_uuid_pk PRIMARY KEY (uuid),
                    CONSTRAINT user_id_fk FOREIGN KEY (user_uuid) REFERENCES APP_USER(uuid),
                    CONSTRAINT name_uk UNIQUE (name),
                    CONSTRAINT url_port_uk UNIQUE (url,port),
                    CONSTRAINT remote_bot_repository_url_uk UNIQUE (repository_url)
                );
                """;
    }

    private String createGameResultTable() {
        return """
                CREATE TABLE IF NOT EXISTS GAME_RESULT(
                    game_uuid UUID NOT NULL,
                    game_start TIMESTAMP NOT NULL,
                    game_end TIMESTAMP,
                    winner_uuid UUID,
                    player1_uuid UUID NOT NULL,
                    player1_score INTEGER,
                    player2_uuid UUID NOT NULL,
                    player2_score INTEGER,
                    CONSTRAINT game_uuid_pk PRIMARY KEY (game_uuid)
                );
                """;
    }

    private String createHandResultsTable() {
        return """
                CREATE TABLE IF NOT EXISTS HAND_RESULT(
                    id SERIAL,
                    r1_c1 VARCHAR(2),
                    r1_c2 VARCHAR(2),
                    r2_c1 VARCHAR(2),
                    r2_c2 VARCHAR(2),
                    r3_c1 VARCHAR(2),
                    r3_c2 VARCHAR(2),
                    game_uuid UUID NOT NULL,
                    hand_type VARCHAR(9) NOT NULL,
                    hand_winner UUID,
                    points INTEGER NOT NULL,
                    points_proposal INTEGER,
                    r1_winner UUID,
                    r2_winner UUID,
                    r3_winner UUID,
                    vira VARCHAR(2),
                    CONSTRAINT hand_results_id_pk PRIMARY KEY(id)
                );
                """;
    }

    private String createRankBotsTable() {
        return """
                CREATE TABLE IF NOT EXISTS BOT_RANK(
                    rank INTEGER,
                    bot_name VARCHAR(30),
                    wins INTEGER,
                    CONSTRAINT bot_rank_pk PRIMARY KEY(rank)
                );
                """;
    }

    private String createSessionTable() {
        return """
                CREATE TABLE IF NOT EXISTS SESSION(
                    uuid UUID NOT NULL,
                    player_uuid UUID NOT NULL,
                    expires_at TIMESTAMP NOT NULL,
                    CONSTRAINT session_uuid_pk PRIMARY KEY (uuid),
                    CONSTRAINT session_player_uuid_fk FOREIGN KEY (player_uuid) REFERENCES app_user(uuid)
                        ON DELETE CASCADE,
                    CONSTRAINT session_player_uuid_uk UNIQUE (player_uuid)
                );
                """;
    }

    private String createInviteTable() {
        return """
                CREATE TABLE IF NOT EXISTS INVITE(
                    uuid UUID NOT NULL,
                    host_player_uuid UUID NOT NULL,
                    invited_player_uuid UUID NOT NULL,
                    expires_at TIMESTAMP NOT NULL,
                    CONSTRAINT invite_uuid_pk PRIMARY KEY (uuid),
                    CONSTRAINT host_player_uuid_fk FOREIGN KEY (host_player_uuid) REFERENCES app_user(uuid),
                    CONSTRAINT invited_player_uuid_fk FOREIGN KEY (invited_player_uuid) REFERENCES app_user(uuid),
                    CONSTRAINT self_invite_ck CHECK (host_player_uuid <> invited_player_uuid),
                    CONSTRAINT invite_pair_uk UNIQUE (host_player_uuid, invited_player_uuid)
                );
                """;
    }

    private String createMaoDeOnzeTable() {
        return """
            CREATE TABLE IF NOT EXISTS MAO_DE_ONZE(
                uuid UUID NOT NULL,
                game_uuid UUID NOT NULL,
                weak_card INTEGER NOT NULL,
                medium_card INTEGER NOT NULL,
                strong_card INTEGER NOT NULL,
                player_type TEXT NOT NULL,
                player_points INTEGER NOT NULL,
                opponent_points INTEGER NOT NULL,
                open_hand BOOLEAN NOT NULL,
                hand_winner BOOLEAN NOT NULL,
                CONSTRAINT mao_de_onze_uuid_pk PRIMARY KEY (uuid)
            );
        """;
    }

    private String createPlayedCardTable() {
        return """
            CREATE TABLE IF NOT EXISTS PLAYED_CARD (
                uuid UUID NOT NULL,
                game_uuid UUID NOT NULL,
                weak_card INTEGER NOT NULL,
                medium_card INTEGER NOT NULL,
                strong_card INTEGER NOT NULL,
                player_type TEXT NOT NULL,
                open_hand BOOLEAN NOT NULL,
                deck_pile INTEGER NOT NULL,
                round_number INTEGER NOT NULL,
                winner_r1 NUMERIC(2,1) NOT NULL,
                winner_r2 NUMERIC(2,1) NOT NULL,
                winner_r3 NUMERIC(2,1) NOT NULL,
                choice INTEGER NOT NULL,
                choice_is_closed INTEGER NOT NULL,
                hand_winner BOOLEAN NOT NULL,
                CONSTRAINT played_card_uuid_pk PRIMARY KEY (uuid)
            );
            """;
    }

    private String createIncreasedPointsTable() {
        return """
        CREATE TABLE IF NOT EXISTS INCREASED_POINTS (
            uuid UUID NOT NULL,
            game_uuid UUID NOT NULL,
            weak_card INTEGER NOT NULL,
            medium_card INTEGER NOT NULL,
            strong_card INTEGER NOT NULL,
            player_type TEXT NOT NULL,
            open_hand BOOLEAN NOT NULL,
            pile INTEGER[] NOT NULL,
            winner_r1 NUMERIC(2,1) NOT NULL,
            winner_r2 NUMERIC(2,1) NOT NULL,
            winner_r3 NUMERIC(2,1) NOT NULL,
            player_points INTEGER NOT NULL,
            opponent_points INTEGER NOT NULL,
            hand_value INTEGER NOT NULL,
            opponent_accepted INTEGER NOT NULL,
            general_score_impact INTEGER NOT NULL,
            CONSTRAINT increased_points_pk PRIMARY KEY (uuid)
        );
        """;
    }

//    private String createTournamentTable() {
//        return """
//                CREATE TABLE IF NOT EXISTS Tournament(
//                    uuid UUID NOT NULL,
//                    size INTEGER NOT NULL,
//                    CONSTRAINT tournament_pk PRIMARY KEY (uuid)
//                );
//                """;
//    }

//    private String createTournamentParticipantsTable() {
//        return """
//                CREATE TABLE IF NOT EXISTS Tournament_participant(
//                    participant_name VARCHAR(30) NOT NULL,
//                    tournament_uuid UUID NOT NULL,
//                    CONSTRAINT tournament_participants_pk PRIMARY KEY (participant_name, tournament_uuid)
//                );
//                """;
//    }
//
//    private String createTournamentMatchesTable() {
//        return """
//                CREATE TABLE IF NOT EXISTS Tournament_match(
//                    tournament_uuid UUID NOT NULL,
//                    match_uuid UUID NOT NULL,
//                    CONSTRAINT tournament_matches_pk PRIMARY KEY (tournament_uuid, match_uuid)
//                );
//                """;
//    }
//
//    private String createTournamentMatchTable() {
//        return """
//                CREATE TABLE IF NOT EXISTS matches(
//                    uuid UUID NOT NULL,
//                    p1_name VARCHAR(30),
//                    p2_name VARCHAR(30),
//                    available BOOLEAN NOT NULL,
//                    winner_name VARCHAR(30),
//                    p1_score INTEGER NOT NULL,
//                    p2_score INTEGER NOT NULL,
//                    uuid_next UUID,
//                    CONSTRAINT tournament_match_pk PRIMARY KEY (uuid)
//                );
//                """;
//    }

}

