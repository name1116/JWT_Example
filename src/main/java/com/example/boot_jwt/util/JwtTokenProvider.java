package com.example.boot_jwt.util;

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

	public String createToken(String username) {
		Date now = new Date();
//		Date expiryDate = new Date(now.getTime() + 3600000);
		Date expiryDate = new Date(now.getTime() + 60000000);

		return Jwts.builder().setSubject(username).setIssuedAt(now).setExpiration(expiryDate).signWith(key).compact();
	}

	public boolean validateToken(String token) {

		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public String getUsernameFromToken(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(