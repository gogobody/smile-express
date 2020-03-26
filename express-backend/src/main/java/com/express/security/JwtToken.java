package com.express.security;

import com.express.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Date;

/**
 * @user: 180296-Web寻梦狮
 * @date: 2018-02-03 15:37
 * @description: 生成token
 */
public class JwtToken {
    private static Key secret = MacProvider.generateKey() ;
    /**
     * 生成jwt的token
     * @param user
     * @return token string
     */
    public static String createToken(User user){
        System.out.println(secret);
        Date now = new Date();
        Date exp = new Date(System.currentTimeMillis() + (1000 * 30*60 )); // 30 minutes
        long current = System.currentTimeMillis();
        if (user!=null) {
            String token = Jwts.builder()
                    .setSubject("visualization")
                    .claim("id", String.valueOf(user.getId()))
                    .claim("name", user.getName())
                    .claim("password", user.getPassword())
                    .claim("createTime", current)
                    .setExpiration(exp)
                    .signWith(SignatureAlgorithm.HS512, secret)
                    .compact();
            return token;

        }
        return null;
    }


    public static Claims parseToken(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException, UnsupportedEncodingException {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims;
    }
}