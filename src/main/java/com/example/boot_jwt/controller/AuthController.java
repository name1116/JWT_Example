package com.example.boot_jwt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.boot_jwt.util.JwtTokenProvider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final JwtTokenProvider tokenProvider;
	
	@PostMapping("/login") // auth login
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
		
		if(loginRequest.username.equals("user") && loginRequest.password.equals("password")) { //인증 성공시!
			String token = tokenProvider.createToken(loginRequest.username); //username으로 토큰을 만드고
			return ResponseEntity.ok(new JwtResponse(token)); // 그 토큰을 클라이언트에게 전달
		}
		
		//유효하지 않은 계정 정보일 경우에는
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 인증 정보");
		
	}
	
	@Getter @Setter
	public static class LoginRequest {
		private String username;
		private String password;
	}
	
	
	
	@Getter
	public static class JwtResponse {
		private String token;
		private JwtResponse(String token) {
			this.token=token;
		}
	}
	
}
