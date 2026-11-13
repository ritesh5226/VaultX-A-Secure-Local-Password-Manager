
package com.vaultx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/vaultx_db";

    private static final String USER_ENV = "VAULTX_DB_USER";
    private static final String PASSWORD_ENV = "VAULTX_DB_PASSWORD";

    private DatabaseConnection() {
        // Prevent creating instances of this utility class.
    }

    public static Connection getConnection() throws SQLException {
        String username = System.getenv(USER_ENV);
        String password = System.getenv(PASSWORD_ENV);

        if (username == null || username.isBlank()
                || password == null || password.isEmpty()) {
            throw new SQLException(
                    "Database credentials are missing. "
                    + "Set VAULTX_DB_USER and VAULTX_DB_PASSWORD."
            );
        }

        return DriverManager.getConnection(URL, username, password);
    }
}
