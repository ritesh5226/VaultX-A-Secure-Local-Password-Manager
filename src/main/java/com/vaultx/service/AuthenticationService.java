
package com.vaultx.service;

import com.vaultx.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class AuthenticationService {

    private final PasswordService passwordService;
    private final EncryptionService encryptionService;

    // Current logged-in user
    private Long currentUserId;
    private String currentUsername;

    // Encryption key exists only during the logged-in session
    private byte[] currentEncryptionKey;

    public AuthenticationService() {
        passwordService = new PasswordService();
        encryptionService = new EncryptionService();
    }

    // =====================================================
    // REGISTER
    // =====================================================

    public boolean register(String username, char[] masterPassword) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty."
            );
        }

        if (username.length() > 50) {
            throw new IllegalArgumentException(
                    "Username cannot exceed 50 characters."
            );
        }

        if (masterPassword == null || masterPassword.length < 8) {
            throw new IllegalArgumentException(
                    "Master Password must be at least 8 characters."
            );
        }

        // Generate unique encryption salt
        byte[] encryptionSalt =
                encryptionService.generateSalt();

        String userSql =
                "INSERT INTO users " +
                "(username, encryption_salt) VALUES (?, ?)";

        String authSql =
                "INSERT INTO user_authentication " +
                "(user_id, password_verifier) VALUES (?, ?)";

        // PasswordService hashes AND wipes masterPassword
        String passwordVerifier =
                passwordService.hashPassword(masterPassword);

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                long userId;

                // -----------------------------------------
                // Insert user
                // -----------------------------------------

                try (PreparedStatement userStatement =
                             connection.prepareStatement(
                                     userSql,
                                     Statement.RETURN_GENERATED_KEYS
                             )) {

                    userStatement.setString(1, username);
                    userStatement.setBytes(2, encryptionSalt);

                    int rowsInserted =
                            userStatement.executeUpdate();

                    if (rowsInserted != 1) {
                        connection.rollback();
                        return false;
                    }

                    try (ResultSet keys =
                                 userStatement.getGeneratedKeys()) {

                        if (!keys.next()) {
                            connection.rollback();

                            throw new SQLException(
                                    "Failed to retrieve generated user ID."
                            );
                        }

                        userId = keys.getLong(1);
                    }
                }

                // -----------------------------------------
                // Insert authentication information
                // -----------------------------------------

                try (PreparedStatement authStatement =
                             connection.prepareStatement(authSql)) {

                    authStatement.setLong(1, userId);
                    authStatement.setString(
                            2,
                            passwordVerifier
                    );

                    int authRowsInserted =
                            authStatement.executeUpdate();

                    if (authRowsInserted != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                connection.commit();

                return true;

            } catch (Exception e) {

                connection.rollback();

                throw e;
            }

        } catch (SQLException e) {

            if ("23000".equals(e.getSQLState())) {

                throw new IllegalArgumentException(
                        "Username already exists."
                );
            }

            throw new RuntimeException(
                    "Account creation failed.",
                    e
            );
        }
    }

    // =====================================================
    // LOGIN
    // =====================================================

    public boolean login(
            String username,
            char[] masterPassword) {

        // Clear any existing session before attempting a new login.
        logout();

        if (username == null ||
                username.isBlank() ||
                masterPassword == null) {

            return false;
        }

        String sql = """
                SELECT u.user_id,
                       u.username,
                       u.encryption_salt,
                       a.password_verifier
                FROM users u
                JOIN user_authentication a
                    ON u.user_id = a.user_id
                WHERE u.username = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);

            try (ResultSet result =
                         statement.executeQuery()) {

                // User doesn't exist
                if (!result.next()) {
                    Arrays.fill(masterPassword, '\0');
                    return false;
                }

                long userId =
                        result.getLong("user_id");

                String storedVerifier =
                        result.getString(
                                "password_verifier"
                        );

                byte[] encryptionSalt =
                        result.getBytes(
                                "encryption_salt"
                        );

                // Verify master password
                boolean passwordCorrect =
                        passwordService.verifyPassword(
                                masterPassword,
                                storedVerifier
                        );

                if (!passwordCorrect) {
                    Arrays.fill(masterPassword, '\0');
                    return false;
                }

                // Derive encryption key from:
                // Master Password + user's persistent salt
                byte[] encryptionKey =
                        encryptionService.deriveKey(
                                masterPassword,
                                encryptionSalt
                        );

                // Login successful
                currentUserId = userId;

                currentUsername =
                        result.getString("username");

                currentEncryptionKey =
                        encryptionKey;

                return true;
            }

        } catch (SQLException e) {

            if (masterPassword != null) {
                Arrays.fill(masterPassword, '\0');
            }

            throw new RuntimeException(
                    "Login failed.",
                    e
            );
        }
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    public void logout() {

        currentUserId = null;
        currentUsername = null;

        if (currentEncryptionKey != null) {

            Arrays.fill(
                    currentEncryptionKey,
                    (byte) 0
            );

            currentEncryptionKey = null;
        }
    }

    // =====================================================
    // SESSION
    // =====================================================

    public boolean isLoggedIn() {
        return currentUserId != null;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public byte[] getCurrentEncryptionKey() {

        if (currentEncryptionKey == null) {
            throw new IllegalStateException(
                    "No encryption key available. " +
                    "User is not logged in."
            );
        }

        return currentEncryptionKey;
    }
}
