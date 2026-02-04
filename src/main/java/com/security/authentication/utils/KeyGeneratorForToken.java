package com.security.authentication.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class KeyGeneratorForToken {
    public static void main(String[] args) throws Exception {
        // 1. Initialize Generator for 2048 bits
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // <--- This is the magic number

        // 2. Generate Pair
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // 3. Get Base64 Strings
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        System.out.println("--- NEW 2048-BIT KEYS ---");
        System.out.println("JWT_SECRET_PRIVATE=" + privateKey);
        System.out.println("JWT_SECRET_PUBLIC=" + publicKey);
    }
}
