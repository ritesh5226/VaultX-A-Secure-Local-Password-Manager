
package com.vaultx.service;

public class AuthenticationServiceTest {

    public static void main(String[] args) {

        AuthenticationService authService =
                new AuthenticationService();

        String username =
                "vaulttest_" + System.currentTimeMillis();

        char[] password =
                "TestMasterPassword123!".toCharArray();

        System.out.println("=== REGISTER TEST ===");

        boolean registered =
                authService.register(
                        username,
                        password
                );

        System.out.println(
                "Registration successful: "
                + registered
        );

        // -------------------------------------------------
        // LOGIN TEST
        // -------------------------------------------------

        System.out.println("\n=== CORRECT LOGIN TEST ===");

        char[] loginPassword =
                "TestMasterPassword123!".toCharArray();

        boolean loggedIn =
                authService.login(
                        username,
                        loginPassword
                );

        System.out.println(
                "Login successful: "
                + loggedIn
        );

        System.out.println(
                "Logged in: "
                + authService.isLoggedIn()
        );

        System.out.println(
                "Current user: "
                + authService.getCurrentUsername()
        );

        System.out.println(
                "Current user ID: "
                + authService.getCurrentUserId()
        );

        System.out.println(
                "Encryption key available: "
                + (authService.getCurrentEncryptionKey()
                        != null)
        );

        System.out.println(
                "Encryption key length: "
                + authService.getCurrentEncryptionKey()
                        .length
        );

        // -------------------------------------------------
        // LOGOUT TEST
        // -------------------------------------------------

        System.out.println("\n=== LOGOUT TEST ===");

        authService.logout();

        System.out.println(
                "Logged in after logout: "
                + authService.isLoggedIn()
        );

        // -------------------------------------------------
        // WRONG PASSWORD TEST
        // -------------------------------------------------

        System.out.println(
                "\n=== WRONG PASSWORD TEST ==="
        );

        char[] wrongPassword =
                "WrongPassword123!".toCharArray();

        boolean wrongLogin =
                authService.login(
                        username,
                        wrongPassword
                );

        System.out.println(
                "Login with wrong password: "
                + wrongLogin
        );

        System.out.println(
                "Still logged in: "
                + authService.isLoggedIn()
        );

        // -------------------------------------------------
        // TEST COMPLETE
        // -------------------------------------------------

        System.out.println(
                "\n=== AUTHENTICATION TEST COMPLETE ==="
        );
    }
}
