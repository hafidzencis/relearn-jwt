package com.exercise.jwt.config;


import com.exercise.jwt.model.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JWTService {


    private static final String SECRET_KEY =  "67556B58703273357538782F413F4428472B4B6250655368566D597133743677";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public boolean isValidToken(String token, UserDetails userDetails){
        String username = extractUsername(token);
        return  username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //generate token not use claim
    public String generateToken(Optional<Users> userDetails){
        return generateToken(new HashMap<>(), userDetails);

    }
    public String generateToken(
            Map<String,Object> extractClaims,
            Optional<Users> userDetails
    ){
        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.get().getEmail())
                .claim("password",userDetails.get().getPassword())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 *24))
                .signWith(getSignInKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    //extract all data payload
    private Claims extractAllClaims(String token){
       return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
