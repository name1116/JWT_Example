package com.example.boot_jwt_db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.boot_jwt_db.model.RefreshToken;


@Repository
public interface RefreshTokenRepository
	extends JpaRepository<RefreshToken, Long>{
	public Optional<RefreshToken> findByToken(String token);
	void deleteByUsername(String name);
}
