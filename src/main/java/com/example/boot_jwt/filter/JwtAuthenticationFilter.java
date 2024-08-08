package com.example.boot_jwt.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.boot_jwt.util.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	private final JwtTokenProvider tokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = getJwtFromRequest(request);
		
		//정상적인 토큰만이 처리 가능
		if (token != null && tokenProvider.validateToken(token)) {
			String username = tokenProvider.getUsernameFromToken(token);
			
			UsernamePasswordAuthenticationToken authentication 
			= new UsernamePasswordAuthenticationToken(username, null, null);
			
			authentication.setDetails(
						new WebAuthenticationDetailsSource()
						.buildDetails(request)
					);
			
			SecurityContextHolder.getContext()
				.setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
		
	}
	
	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7); //<Bearer > 이렇게 시작하기 때문에 앞 7글자를 지움
		}
		return null;
	}
	
}





