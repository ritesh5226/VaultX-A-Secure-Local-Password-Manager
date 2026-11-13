
package com.vaultx.service;

public class PasswordServiceTest {

    public static void main(String[] args) {

        PasswordService passwordService = new PasswordService();

        // Create a test password
        char[] password = "TestPassword123!".toCharArray();

        // Generate password hash
        String hashedPassword = passwordService.hashPassword(password);

        System.out.println("Generated Hash:");
        System.out.println(hashedPassword);

        // Verify correct password
        boolean correctPassword = passwordService.verifyPassword(
            "TestPassword123!".toCharArray(),
            hashedPassword
        );

        System.out.println("Correct password: " + correctPassword);

        // Verify incorrect password
        boolean incorrectPassword = passwordService.verifyPassword(
            "WrongPassword123!".toCharArray(),
            hashedPassword
        );

        System.out.println("Incorrect password: " + incorrectPassword);
    }
}
