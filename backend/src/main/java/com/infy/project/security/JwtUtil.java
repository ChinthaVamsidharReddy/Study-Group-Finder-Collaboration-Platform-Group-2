//com/infy/project/security/JwtUtil.java
package com.infy.project.security;



import io.jsonwebtoken.*;
import java.util.Date;

public class JwtUtil {
 private final String SECRET_KEY = "secret";

 public String extractUsername(String token) {
     return Jwts.parser().setSigningKey(SECRET_KEY)
             .parseClaimsJws(token).getBody().getSubject();
 }

 public boolean validateToken(String token) {
     try {
         Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
         return true;
     } catch (Exception e) {
         return false;
     }
 }

 public String generateToken(String username) {
     return Jwts.builder()
             .setSubject(username)
             .setIssuedAt(new Date(System.currentTimeMillis()))
             .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
             .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
             .compact();
 }
}
