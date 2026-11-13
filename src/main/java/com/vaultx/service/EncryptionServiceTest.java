package com.vaultx.service;

import java.util.Arrays;

public class EncryptionServiceTest {

    public static void main(String[] args) {

        EncryptionService encryptionService =
                new EncryptionService();

        char[] masterPassword =
                "TestMasterPassword123!".toCharArray();

        char[] vaultPassword =
                "MySecretVaultPassword!".toCharArray();

        // Generate encryption salt
        byte[] salt =
                encryptionService.generateSalt();

        // Derive encryption key
        byte[] encryptionKey =
                encryptionService.deriveKey(
                        masterPassword,
                        salt
                );

        System.out.println(
                "Encryption key generated: "
                + (encryptionKey != null)
        );

        // Encrypt
        EncryptionService.EncryptedData encrypted =
                encryptionService.encrypt(
                        vaultPassword,
                        encryptionKey
                );

        System.out.println(
                "Encrypted password length: "
                + encrypted.getCiphertext().length
        );

        System.out.println(
                "IV length: "
                + encrypted.getIv().length
        );

        // Decrypt
        char[] decrypted =
                encryptionService.decrypt(
                        encrypted.getCiphertext(),
                        encrypted.getIv(),
                        encryptionKey
                );

        System.out.println(
                "Decrypted password: "
                + new String(decrypted)
        );

        boolean successful =
                Arrays.equals(
                        "MySecretVaultPassword!"
                                .toCharArray(),
                        decrypted
                );

        System.out.println(
                "Encryption/Decryption successful: "
                + successful
        );

        // Clean sensitive data
        Arrays.fill(encryptionKey, (byte) 0);
        Arrays.fill(decrypted, '\0');
    }
}
