package com.example.boot_jwt_db.util;


import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtTokenProvider {

	private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	public String createToken(String username, String role, Long userId) {
		Claims claims = Jwts.claims().setSubject(username);
		claims.put("role", role);
		claims.put("userId", userId);

		Date now = new Date();
		Date validity = new Date(now.getTime() + 3_600_000);

		return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(validity)
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); // 만약 양식을 안지키면 에러가 남
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public Claims getClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build()
				.parseClaimsJws(token).getBody();
	}
	
	public String getUsername(String token) {
//		return Jwts.parserBuilder().setSigningKey(key).build()
//				.parseClaimsJws(token).getBody().getSubject();
		return getClaims(token).getSubject();		
	}
	
	public String getRole(String token) {
//		return Jwts.parserBuilder().setSigningKey(key).build()
//				.parseClaimsJws(token).getBody()
//				.get("role", String.class);
		return getClaims(token).get("role", String.class);
	}
	
	public Long getUserId(String token) {
		return getClaims(token).get("userId", Long.class);
	}
	
	public String createRefreshToken(String username, String role, Long userId) {
		Claims claims = Jwts.claims().setSubject(username);
		claims.put("role", role);
		claims.put("userId", userId);

		Date now = new Date();
		Date validity = new Date(now.getTime() + 7_200_000);

		return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(validity)
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}
}