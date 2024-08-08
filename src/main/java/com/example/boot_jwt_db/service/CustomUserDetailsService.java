package com.example.boot_jwt_db.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.boot_jwt_db.model.UserAccount;
import com.example.boot_jwt_db.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{
	private final UserAccountRepository userAccountRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserAccount user = userAccountRepository.findByUsername(username)
				.orElseThrow(()-> new UsernameNotFoundException("해당 유저 없음"));
		UserBuilder builder = User.withUsername(username);
		builder.password(user.getPassword());
		builder.roles(user.getRole());
		return builder.build();
	}
}
