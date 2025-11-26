package com.example.demo.service;

import com.example.demo.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
public class JwtService {
    @Value("${jwt.secret}")
    public String secret;
     public String generateToken(User user){
       return Jwts.builder()
               .subject(String.valueOf(user.getId()))
               .claim("Firstname",user.getFirstname())
               .claim("Lastname",user.getLastname())
               .claim("email",user.getEmail())
               .claim("role",user.getRole())
               .issuedAt(new Date(System.currentTimeMillis()))
               .expiration(new Date(System.currentTimeMillis()+64000*1000))
               .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
               .compact();

     }
     public Claims getPayload (String token){
         return Jwts.parser()
                 .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                 .build()
                 .parseSignedClaims(token)
                 .getPayload();

     }
     public boolean verify( String token){
      var expirydate= getPayload(token).getExpiration();
      var afterdate=expirydate.before(new Date());
      return afterdate;
     }

     public String getEmailFromToken (String token){
         var claims=getPayload(token);

         return claims.get("email", String.class);
     }
     public String getRole (String token){
         var claims=getPayload(token);

         return claims.get("role", String.class);
     }

}
