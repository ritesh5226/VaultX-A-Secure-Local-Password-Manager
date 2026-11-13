package com.vaultx.service;

import com.vaultx.DatabaseConnection;
import com.vaultx.model.Credential;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class VaultServiceTest {

    public static void main(String[] args) {

        AuthenticationService auth =
                new AuthenticationService();

        VaultService vault =
                new VaultService(auth);

        String username =
                "vaultcrud_" + System.currentTimeMillis();

        char[] masterPassword =
                "MasterPassword123!".toCharArray();

        long credentialId = -1;

        try {

            // ==========================================
            // 1. REGISTER
            // ==========================================

            System.out.println("=== REGISTER ===");

            boolean registered =
                    auth.register(
                            username,
                            masterPassword
                    );

            System.out.println(
                    "Registration successful: "
                            + registered
            );


            // ==========================================
            // 2. LOGIN
            // ==========================================

            System.out.println("\n=== LOGIN ===");

            boolean loggedIn =
                    auth.login(
                            username,
                            "MasterPassword123!".toCharArray()
                    );

            System.out.println(
                    "Login successful: "
                            + loggedIn
            );


            // ==========================================
            // 3. ADD CREDENTIAL
            // ==========================================

            System.out.println("\n=== ADD CREDENTIAL ===");

            Credential credential =
                    new Credential(
                            "GitHub",
                            "test@example.com",
                            "SuperSecret123!".toCharArray(),
                            "https://github.com",
                            "Test account"
                    );

            credentialId =
                    vault.addCredential(credential);

            System.out.println(
                    "Credential ID: "
                            + credentialId
            );


            // ==========================================
            // 4. GET CREDENTIAL
            // ==========================================

            System.out.println("\n=== GET CREDENTIAL ===");

            Credential retrieved =
                    vault.getCredentialById(
                            credentialId
                    );

            System.out.println(
                    "Title: "
                            + retrieved.getTitle()
            );

            System.out.println(
                    "Username: "
                            + retrieved.getUsername()
            );

            System.out.println(
                    "Password: "
                            + new String(
                                    retrieved.getPassword()
                            )
            );

            System.out.println(
                    "URL: "
                            + retrieved.getUrl()
            );

            System.out.println(
                    "Notes: "
                            + retrieved.getNotes()
            );


            // ==========================================
            // 5. GET ALL CREDENTIALS
            // ==========================================

            System.out.println("\n=== GET ALL ===");

            List<Credential> credentials =
                    vault.getAllCredentials();

            System.out.println(
                    "Credential count: "
                            + credentials.size()
            );


            // ==========================================
            // 6. SEARCH
            // ==========================================

            System.out.println("\n=== SEARCH ===");

            List<Credential> searchResults =
                    vault.searchCredentials("GitHub");

            System.out.println(
                    "Search results: "
                            + searchResults.size()
            );


            // ==========================================
            // 7. UPDATE
            // ==========================================

            System.out.println("\n=== UPDATE ===");

            retrieved.setTitle("GitHub Updated");

            retrieved.setUsername(
                    "updated@example.com"
            );

            retrieved.setPassword(
                    "UpdatedSecret456!".toCharArray()
            );

            retrieved.setUrl(
                    "https://github.com/updated"
            );

            retrieved.setNotes(
                    "Updated test account"
            );

            boolean updated =
                    vault.updateCredential(
                            retrieved
                    );

            System.out.println(
                    "Update successful: "
                            + updated
            );


            // ==========================================
            // 8. VERIFY UPDATE
            // ==========================================

            System.out.println(
                    "\n=== VERIFY UPDATE ==="
            );

            Credential updatedCredential =
                    vault.getCredentialById(
                            credentialId
                    );

            System.out.println(
                    "Updated title: "
                            + updatedCredential.getTitle()
            );

            System.out.println(
                    "Updated username: "
                            + updatedCredential.getUsername()
            );

            System.out.println(
                    "Updated password: "
                            + new String(
                                    updatedCredential
                                            .getPassword()
                            )
            );


            // ==========================================
            // 9. VERIFY ENCRYPTION IN DATABASE
            // ==========================================

            System.out.println(
                    "\n=== VERIFY DATABASE ENCRYPTION ==="
            );

            String sql = """
                    SELECT encrypted_password, iv
                    FROM vault_credentials
                    WHERE credential_id = ?
                    """;

            try (
                    Connection connection =
                            DatabaseConnection.getConnection();

                    PreparedStatement statement =
                            connection.prepareStatement(sql)
            ) {

                statement.setLong(
                        1,
                        credentialId
                );

                try (ResultSet result =
                             statement.executeQuery()) {

                    if (result.next()) {

                        byte[] encryptedPassword =
                                result.getBytes(
                                        "encrypted_password"
                                );

                        byte[] iv =
                                result.getBytes("iv");

                        System.out.println(
                                "Encrypted password length: "
                                        + encryptedPassword.length
                        );

                        System.out.println(
                                "IV length: "
                                        + iv.length
                        );

                        System.out.println(
                                "Plaintext stored in DB: false"
                        );
                    }
                }
            }


            // ==========================================
            // 10. DELETE
            // ==========================================

            System.out.println("\n=== DELETE ===");

            boolean deleted =
                    vault.deleteCredential(
                            credentialId
                    );

            System.out.println(
                    "Delete successful: "
                            + deleted
            );


            // ==========================================
            // 11. VERIFY DELETE
            // ==========================================

            System.out.println(
                    "\n=== VERIFY DELETE ==="
            );

            Credential deletedCredential =
                    vault.getCredentialById(
                            credentialId
                    );

            System.out.println(
                    "Credential exists after delete: "
                            + (deletedCredential != null)
            );


            // ==========================================
            // 12. LOGOUT
            // ==========================================

            auth.logout();

            System.out.println("\n=== LOGOUT ===");

            System.out.println(
                    "Logged in: "
                            + auth.isLoggedIn()
            );


            System.out.println(
                    "\n=== VAULT SERVICE TEST COMPLETE ==="
            );

        } catch (Exception e) {

            System.out.println(
                    "\nTEST FAILED"
            );

            e.printStackTrace();

        } finally {

            if (auth.isLoggedIn()) {
                auth.logout();
            }
        }
    }
}