package com.vaultx;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PreparedStatementTest {

    public static void main(String[] args) {

        String username = "testuser";

        String sql = "INSERT INTO users (username) VALUES (?)";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);

            int rowsInserted = statement.executeUpdate();

            System.out.println("Rows inserted: " + rowsInserted);

        } catch (Exception e) {
            System.out.println("Database operation failed.");
            e.printStackTrace();
        }
    }
}