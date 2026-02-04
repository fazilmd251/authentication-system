package com.security.authentication.utils;

import com.security.authentication.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final int EXPIRY = 1000 * 60 * 10 ; // 10 min
    private final int REFRESH_EXPIRY = 1000 * 60 * 60 * 10; // 10 Hours

    @Value("${JWT_SECRET_PRIVATE}")
    private String privateKeyStr;

    @Value("${JWT_SECRET_PUBLIC}")
    private String publicKeyStr;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        this.privateKey = loadPrivateKey(privateKeyStr);
        this.publicKey = loadPublicKey(publicKeyStr);
    }
public String generateRefreshToken(User user){
    return Jwts.builder()
            .subject(user.getEmail())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRY))
            .signWith(privateKey, Jwts.SIG.RS256)
            .compact();
}
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRY))
                .signWith(privateKey, Jwts.SIG.RS256) // Use RS256 for Asymmetric
                .compact();
    }



    private PrivateKey loadPrivateKey(String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(cleanKey(key));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
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



//package com.security.authentication.utils;
//
//
//import com.security.authentication.model.User;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class JwtService {
//
//    private final int EXPIRY=1000*60*5;
//
//    @Value("${JWT_SECRET_PRIVATE}")
//    private  String privateKey;
//
//    @Value("${JWT_SECRET_PUBLIC}")
//    private  String publicKey;
//
//    public String generateToken(User user){
//        Map<String,Object> claims=new HashMap<>();
//        claims.put("role",user.getRole());
//        claims.put("email",user.getEmail());
//        claims.put("id",user.getId());
//
//       return  Jwts.builder().claims(claims)
//                .subject(user.getEmail())
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis()+EXPIRY))
//                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
//                .compact();
//    }
//
//    public JwtService() {
//    }
//}
