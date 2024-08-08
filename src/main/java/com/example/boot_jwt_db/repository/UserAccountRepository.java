package com.example.boot_jwt_db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.boot_jwt_db.model.UserAccount;


@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long>{
	//findByUsername를 따로 만들어줘야함. 
	
	public Optional<UserAccount> findByUsername(String token);
}
