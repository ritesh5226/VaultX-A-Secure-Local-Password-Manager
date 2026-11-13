package com.vaultx.service;

import com.vaultx.DatabaseConnection;
import com.vaultx.model.Credential;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VaultService {

    private final AuthenticationService authenticationService;
    private final EncryptionService encryptionService;

    public VaultService(
            AuthenticationService authenticationService) {

        if (authenticationService == null) {
            throw new IllegalArgumentException(
                    "AuthenticationService cannot be null."
            );
        }

        this.authenticationService =
                authenticationService;

        this.encryptionService =
                new EncryptionService();
    }

    // =====================================================
    // ADD CREDENTIAL
    // =====================================================

    public long addCredential(Credential credential) {

        requireLoggedIn();

        if (credential == null) {
            throw new IllegalArgumentException(
                    "Credential cannot be null."
            );
        }

        if (credential.getTitle() == null ||
                credential.getTitle().isBlank()) {

            throw new IllegalArgumentException(
                    "Title cannot be empty."
            );
        }

        EncryptionService.EncryptedData encrypted =
                encryptionService.encrypt(
                        credential.getPassword(),
                        authenticationService
                                .getCurrentEncryptionKey()
                );

        String sql = """
                INSERT INTO vault_credentials
                (
                    user_id,
                    title,
                    username,
                    encrypted_password,
                    iv,
                    url,
                    notes
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                java.sql.Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setLong(
                    1,
                    authenticationService
                            .getCurrentUserId()
            );

            statement.setString(
                    2,
                    credential.getTitle()
            );

            statement.setString(
                    3,
                    credential.getUsername()
            );

            statement.setBytes(
                    4,
                    encrypted.getCiphertext()
            );

            statement.setBytes(
                    5,
                    encrypted.getIv()
            );

            statement.setString(
                    6,
                    credential.getUrl()
            );

            statement.setString(
                    7,
                    credential.getNotes()
            );

            int rowsInserted =
                    statement.executeUpdate();

            if (rowsInserted != 1) {
                throw new RuntimeException(
                        "Failed to add credential."
                );
            }

            try (ResultSet keys =
                         statement.getGeneratedKeys()) {

                if (!keys.next()) {
                    throw new RuntimeException(
                            "Failed to retrieve credential ID."
                    );
                }

                long credentialId =
                        keys.getLong(1);

                credential.setCredentialId(
                        credentialId
                );

                return credentialId;
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to add credential.",
                    e
            );
        }
    }

    // =====================================================
    // GET ALL CREDENTIALS
    // =====================================================

    public List<Credential> getAllCredentials() {

        requireLoggedIn();

        String sql = """
                SELECT credential_id,
                       title,
                       username,
                       encrypted_password,
                       iv,
                       url,
                       notes
                FROM vault_credentials
                WHERE user_id = ?
                ORDER BY title
                """;

        List<Credential> credentials =
                new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    authenticationService
                            .getCurrentUserId()
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {

                    credentials.add(
                            buildCredential(result)
                    );
                }
            }

            return credentials;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to retrieve credentials.",
                    e
            );
        }
    }

    // =====================================================
    // GET CREDENTIAL BY ID
    // =====================================================

    public Credential getCredentialById(
            long credentialId) {

        requireLoggedIn();

        String sql = """
                SELECT credential_id,
                       title,
                       username,
                       encrypted_password,
                       iv,
                       url,
                       notes
                FROM vault_credentials
                WHERE credential_id = ?
                  AND user_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, credentialId);

            statement.setLong(
                    2,
                    authenticationService
                            .getCurrentUserId()
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                if (!result.next()) {
                    return null;
                }

                return buildCredential(result);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to retrieve credential.",
                    e
            );
        }
    }

    // =====================================================
    // SEARCH CREDENTIALS
    // =====================================================

    public List<Credential> searchCredentials(
            String searchText) {

        requireLoggedIn();

        if (searchText == null ||
                searchText.isBlank()) {

            return getAllCredentials();
        }

        String sql = """
                SELECT credential_id,
                       title,
                       username,
                       encrypted_password,
                       iv,
                       url,
                       notes
                FROM vault_credentials
                WHERE user_id = ?
                  AND (
                      title LIKE ?
                      OR username LIKE ?
                      OR url LIKE ?
                  )
                ORDER BY title
                """;

        String pattern =
                "%" + searchText + "%";

        List<Credential> credentials =
                new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    authenticationService
                            .getCurrentUserId()
            );

            statement.setString(2, pattern);
            statement.setString(3, pattern);
            statement.setString(4, pattern);

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {

                    credentials.add(
                            buildCredential(result)
                    );
                }
            }

            return credentials;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to search credentials.",
                    e
            );
        }
    }

    // =====================================================
    // UPDATE CREDENTIAL
    // =====================================================

    public boolean updateCredential(
            Credential credential) {

        requireLoggedIn();

        if (credential == null) {
            throw new IllegalArgumentException(
                    "Credential cannot be null."
            );
        }

        EncryptionService.EncryptedData encrypted =
                encryptionService.encrypt(
                        credential.getPassword(),
                        authenticationService
                                .getCurrentEncryptionKey()
                );

        String sql = """
                UPDATE vault_credentials
                SET title = ?,
                    username = ?,
                    encrypted_password = ?,
                    iv = ?,
                    url = ?,
                    notes = ?
                WHERE credential_id = ?
                  AND user_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    credential.getTitle()
            );

            statement.setString(
                    2,
                    credential.getUsername()
            );

            statement.setBytes(
                    3,
                    encrypted.getCiphertext()
            );

            statement.setBytes(
                    4,
                    encrypted.getIv()
            );

            statement.setString(
                    5,
                    credential.getUrl()
            );

            statement.setString(
                    6,
                    credential.getNotes()
            );

            statement.setLong(
                    7,
                    credential.getCredentialId()
            );

            statement.setLong(
                    8,
                    authenticationService
                            .getCurrentUserId()
            );

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to update credential.",
                    e
            );
        }
    }

    // =====================================================
    // DELETE CREDENTIAL
    // =====================================================

    public boolean deleteCredential(
            long credentialId) {

        requireLoggedIn();

        String sql = """
                DELETE FROM vault_credentials
                WHERE credential_id = ?
                  AND user_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, credentialId);

            statement.setLong(
                    2,
                    authenticationService
                            .getCurrentUserId()
            );

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to delete credential.",
                    e
            );
        }
    }

    // =====================================================
    // BUILD CREDENTIAL FROM DATABASE
    // =====================================================

    private Credential buildCredential(
            ResultSet result)
            throws SQLException {

        byte[] ciphertext =
                result.getBytes(
                        "encrypted_password"
                );

        byte[] iv =
                result.getBytes("iv");

        char[] password =
                encryptionService.decrypt(
                        ciphertext,
                        iv,
                        authenticationService
                                .getCurrentEncryptionKey()
                );

        return new Credential(
                result.getLong("credential_id"),
                result.getString("title"),
                result.getString("username"),
                password,
                result.getString("url"),
                result.getString("notes")
        );
    }

    // =====================================================
    // SESSION CHECK
    // =====================================================

    private void requireLoggedIn() {

        if (!authenticationService.isLoggedIn()) {

            throw new IllegalStateException(
                    "User must be logged in."
            );
        }
    }
}
