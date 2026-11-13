
package com.vaultx.service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class PasswordService {

    private static final int ITERATIONS = 3;
    private static final int MEMORY = 65536;
    private static final int PARALLELISM = 1;

    // Generate a secure password hash
    public String hashPassword(char[] password) {

        Argon2 argon2 = Argon2Factory.create(
            Argon2Factory.Argon2Types.ARGON2id
        );

        try {
            return argon2.hash(
                ITERATIONS,
                MEMORY,
                PARALLELISM,
                password
            );

        } finally {
            argon2.wipeArray(password);
        }
    }

    // Verify a password against its stored hash
    public boolean verifyPassword(
            char[] password,
            String storedHash) {

        Argon2 argon2 = Argon2Factory.create(
            Argon2Factory.Argon2Types.ARGON2id
        );

        return argon2.verify(
                storedHash,
                password
        );
    }
}
