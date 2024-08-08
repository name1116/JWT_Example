package com.example.boot_jwt_db.service;

import org.springframework.stereotype.Service;

import com.example.boot_jwt_db.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
	private final RefreshTokenRepository repository;
	
	@Transactional
	public void deleteByUsername(String username) {
		repository.deleteByUsername(username);
	}
}
