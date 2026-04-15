package com.bueno.persistence;

import java.sql.*;

public class ConnectionFactory implements AutoCloseable {

    private static Connection connection;
    private static PreparedStatement preparedStatement;
    private static Statement statement;

    public static Connection createConnection() {
        try {
            instantiateConnectionIfNull();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return connection;
    }

    private static void instantiateConnectionIfNull() throws SQLException {
        if (connection == null) {
            String url = getDatasourceRef("url", "jdbc:postgresql://localhost:5432/ctruco");
            String user = getDatasourceRef("username", "postgres");
            String password = getDatasourceRef("password", "password");
            connection = DriverManager.getConnection(url, user, password);
        }
    }

    public static PreparedStatement createPreparedStatement(String sql) {
        try {
            preparedStatement = createConnection().prepareStatement(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return preparedStatement;
    }

    public static Statement createStatement() {
        try {
            statement = createConnection().createStatement();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return statement;
    }

    @Override
    public void close() throws Exception {
        closeStatementsIfNotNull();
        closeConnectionIfNotNull();
    }

    private static void closeStatementsIfNotNull() throws SQLException {
        if (preparedStatement != null) preparedStatement.close();
        if (statement != null) statement.close();
    }

    private static void closeConnectionIfNotNull() throws SQLException {
        if (connection != null) connection.close();
    }

    private static String getDatasourceRef(String ref, String defaultRef) {
        return System.getenv("SPRING_DATASOURCE_" + ref.toUpperCase()) != null
                ? System.getenv("SPRING_DATASOURCE_" + ref.toUpperCase())
                : defaultRef;
    }
}

