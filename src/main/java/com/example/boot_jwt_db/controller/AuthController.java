package com.example.boot_jwt_db.controller;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.boot_jwt_db.model.RefreshToken;
import com.example.boot_jwt_db.model.UserAccount;
import com.example.boot_jwt_db.repository.RefreshTokenRepository;
import com.example.boot_jwt_db.repository.UserAccountRepository;
import com.example.boot_jwt_db.service.RefreshTokenService;
import com.example.boot_jwt_db.util.JwtTokenProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

	private final JwtTokenProvider tokenProvider;
	private final UserAccountRepository userAccountRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenService refreshTokenService;

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
		Optional<UserAccount> userOptional = userAccountRepository.findByUsername(loginRequest.username);
		if (userOptional.isPresent() // DB에 해당 USERNAME이 있고
				&& passwordEncoder.matches( // 해싱한 password와 입력한게 매칭되는지...
						loginRequest.getPassword(), userOptional.get().getPassword())) {
//			String token = tokenProvider.createToken(loginRequest.username, userOptional.get().getRole(),
//					userOptional.get().getId());
			String accessToken = tokenProvider.createToken(
					loginRequest.username, userOptional.get().getRole(),
					userOptional.get().getId());
			// 리프레시 토큰
			String refreshToken = tokenProvider.createRefreshToken(
					loginRequest.username, userOptional.get().getRole(),
					userOptional.get().getId());
			// 리프레시 토큰을 DB에 넣기
			RefreshToken tokenEntity = new RefreshToken();
			tokenEntity.setToken(refreshToken);
			tokenEntity.setUsername(userOptional.get().getUsername());
			tokenEntity.setExpiryDate(
					new Date(new Date().getTime() + 7_200_000));
			refreshTokenRepository.save(tokenEntity);
			// 응답
			return ResponseEntity.ok(new JwtResponse(
					accessToken, refreshToken));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증에 실패했습니다");
		}
	}

	@PostMapping("/join")
	public ResponseEntity<?> joinUser(@RequestBody JoinRequest joinRequest) {
		if (userAccountRepository.findByUsername(joinRequest.getUsername()).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 등록된 username입니다");
		}
		UserAccount user = new UserAccount();
		user.setUsername(joinRequest.getUsername());
		user.setPassword(passwordEncoder.encode(joinRequest.getPassword()));
		user.setRole(joinRequest.getRole());

		userAccountRepository.save(user);

		return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입 성공");
	}

	@Getter @Setter
	public static class RefreshRequest {
		private String refreshToken;
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<?> refreshAccessToken(
			@RequestBody RefreshRequest refreshRequest) {
		Optional<RefreshToken> tokenOptional = refreshTokenRepository
				.findByToken(refreshRequest.refreshToken);
		if (tokenOptional.isPresent() // Refresh토큰이 DB에 저장되어 있고
				&& tokenProvider.validateToken( // 해당 토큰이 정상이라면
						refreshRequest.refreshToken)) {
			String username = tokenProvider.getUsername(
					refreshRequest.refreshToken);
			Long userId = tokenProvider.getUserId(
					refreshRequest.refreshToken);
			String role = tokenProvider.getRole(
					refreshRequest.refreshToken);
			String newAccessToken = tokenProvider.createToken(
					username, role, userId);
			return ResponseEntity.ok(new JwtResponse(
					newAccessToken, refreshRequest.refreshToken));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증에 실패했습니다");
		}
	}

	  @Getter @Setter
		public static class LogoutRequest {
			private String username;
		}
		
		@PostMapping("/logout")
		public ResponseEntity<?> logout(@RequestBody LogoutRequest
				logoutRequest) {
			// refreshTokenRepository.deleteByUsername(logoutRequest.getUsername());
			refreshTokenService.deleteByUsername(logoutRequest.getUsername());
			return ResponseEntity.ok("로그아웃 성공");
		}
	
	@Getter
	@Setter
	public static class JoinRequest {
		private String username;
		private String password;
		private String role;
	}

	@Getter
	@Setter
	public static class LoginRequest {
		private String username;
		private String password;
	}

	@Getter
	@AllArgsConstructor
	public static class JwtResponse {
		// private String token;
		private String accessToken;
		private String refreshToken;
	}
}
