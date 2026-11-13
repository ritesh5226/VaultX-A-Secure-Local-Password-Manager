
package com.vaultx;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnectionTest {

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet =
                     statement.executeQuery("SELECT DATABASE()")) {

            if (resultSet.next()) {
                System.out.println(
                        "Connected successfully to: "
                        + resultSet.getString(1)
                );
            }

        } catch (Exception e) {
            System.err.println("Database connection failed.");
            e.printStackTrace();
        }
    }
}
