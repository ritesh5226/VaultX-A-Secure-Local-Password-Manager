package com.vaultx.service;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import de.mkammerer.argon2.Argon2Advanced;
import de.mkammerer.argon2.Argon2Factory;

public class EncryptionService {

    private static final int ARGON2_ITERATIONS = 3;
    private static final int ARGON2_MEMORY = 65536;
    private static final int ARGON2_PARALLELISM = 1;

    private static final int KEY_LENGTH = 32; // 256 bits
    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private final SecureRandom secureRandom;

    public EncryptionService() {
        secureRandom = new SecureRandom();
    }

    // -----------------------------------------------------
    // Generate a random salt
    // -----------------------------------------------------

    public byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }

    // -----------------------------------------------------
    // Derive AES-256 key from Master Password
    // -----------------------------------------------------

    public byte[] deriveKey(char[] masterPassword, byte[] salt) {

    if (masterPassword == null || masterPassword.length == 0) {
        throw new IllegalArgumentException(
                "Master Password cannot be empty."
        );
    }

    if (salt == null || salt.length != SALT_LENGTH) {
        throw new IllegalArgumentException(
                "Invalid encryption salt."
        );
    }

    Argon2Advanced argon2 =
            Argon2Factory.createAdvanced(
                    Argon2Factory.Argon2Types.ARGON2id
            );

    try {
        return argon2.rawHash(
                ARGON2_ITERATIONS,
                ARGON2_MEMORY,
                ARGON2_PARALLELISM,
                masterPassword,
                salt
        );

    } finally {
        argon2.wipeArray(masterPassword);
    }
}

    // -----------------------------------------------------
    // Encrypt plaintext password
    // -----------------------------------------------------

    public EncryptedData encrypt(
            char[] plaintextPassword,
            byte[] encryptionKey) {

        if (plaintextPassword == null) {
            throw new IllegalArgumentException(
                    "Password cannot be null."
            );
        }

        validateKey(encryptionKey);

        byte[] plaintext = null;

        try {
            plaintext = new String(plaintextPassword)
                    .getBytes(java.nio.charset.StandardCharsets.UTF_8);

            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            GCMParameterSpec gcmSpec =
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            SecretKeySpec keySpec =
                    new SecretKeySpec(encryptionKey, "AES");

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    keySpec,
                    gcmSpec
            );

            byte[] ciphertext = cipher.doFinal(plaintext);

            return new EncryptedData(ciphertext, iv);

        } catch (GeneralSecurityException e) {

            throw new RuntimeException(
                    "Encryption failed.",
                    e
            );

        } finally {

            Arrays.fill(plaintextPassword, '\0');

            if (plaintext != null) {
                Arrays.fill(plaintext, (byte) 0);
            }
        }
    }

    // -----------------------------------------------------
    // Decrypt encrypted password
    // -----------------------------------------------------

    public char[] decrypt(
            byte[] ciphertext,
            byte[] iv,
            byte[] encryptionKey) {

        if (ciphertext == null || ciphertext.length == 0) {
            throw new IllegalArgumentException(
                    "Encrypted password is missing."
            );
        }

        if (iv == null || iv.length != IV_LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid initialization vector."
            );
        }

        validateKey(encryptionKey);

        try {
            Cipher cipher =
                    Cipher.getInstance("AES/GCM/NoPadding");

            GCMParameterSpec gcmSpec =
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            SecretKeySpec keySpec =
                    new SecretKeySpec(encryptionKey, "AES");

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    keySpec,
                    gcmSpec
            );

            byte[] plaintext =
                    cipher.doFinal(ciphertext);

            return new String(
                    plaintext,
                    java.nio.charset.StandardCharsets.UTF_8
            ).toCharArray();

        } catch (GeneralSecurityException e) {

            throw new RuntimeException(
                    "Decryption failed.",
                    e
            );
        }
    }

    // -----------------------------------------------------
    // Validate AES key
    // -----------------------------------------------------

    private void validateKey(byte[] key) {

        if (key == null || key.length != KEY_LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid encryption key."
            );
        }
    }

    // -----------------------------------------------------
    // Container for encrypted data
    // -----------------------------------------------------

    public static class EncryptedData {

        private final byte[] ciphertext;
        private final byte[] iv;

        public EncryptedData(
                byte[] ciphertext,
                byte[] iv) {

            this.ciphertext = ciphertext;
            this.iv = iv;
        }

        public byte[] getCiphertext() {
            return ciphertext;
        }

        public byte[] getIv() {
            return iv;
        }
    }
}