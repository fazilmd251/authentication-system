package com.security.authentication.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@RestController
public class TestController {

    @Value("${JWT_SECRET_PUBLIC}")
    private String publicKeyStr;

    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        this.publicKey = loadPublicKey(publicKeyStr);
    }

    @GetMapping("/health")
    public String test() {
        return "works";
    }

    public record TokenRequest(String token) {}

    // 2. Use it in Controller
    @PostMapping("/verify-token")
    public ResponseEntity<?> testToken(@RequestBody TokenRequest request) {
        String token = request.token().trim();
        var payload = extractAllClaims(token);
        if (payload != null) {
            return ResponseEntity.ok(payload);
        }
        return ResponseEntity.badRequest().body("Filed to get the data");
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
