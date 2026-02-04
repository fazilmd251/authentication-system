package com.security.authentication.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@NoArgsConstructor
public class ExtractClaims {

    @Value("${JWT_SECRET_PUBLIC}")
    private String publicKeyStr;

    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        this.publicKey = loadPublicKey(publicKeyStr);
    }


    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.publicKey) // Verify using the PUBLIC key
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private PublicKey loadPublicKey(String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(cleanKey(key));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    private String cleanKey(String key) {
        if (key == null) return "";
        return key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", ""); // This removes ALL hidden spaces, tabs, and newlines
    }

}
