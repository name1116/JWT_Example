package com.example.boot_jwt_db.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.boot_jwt_db.model.CustomUserDetails;
import com.example.boot_jwt_db.service.CustomUserDetailsService;
import com.example.boot_jwt_db.util.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	private final JwtTokenProvider tokenProvider;
	private final CustomUserDetailsService customUserDetailsService;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = getJwtFromRequest(request);
		
		if (token != null && tokenProvider.validateToken(token)) {
			String username = tokenProvider.getUsername(token);
			
			UserDetails userDetails = customUserDetailsService
					.loadUserByUsername(username);
			CustomUserDetails details = new CustomUserDetails(
					userDetails,
					tokenProvider.getUserId(token));
			UsernamePasswordAuthenticationToken authentication
				= new UsernamePasswordAuthenticationToken(
					details, null, userDetails.getAuthorities());
			
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			
			authentication.setDetails(new CustomUserDetails(userDetails, tokenProvider.getUserId(token)));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		
		filterChain.doFilter(request, 