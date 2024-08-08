package com.example.boot_jwt_db.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class CustomUserDetails implements UserDetails{
	private final UserDetails userDetails;
	private final Long userId;
	
	public CustomUserDetails(UserDetails userDetails, Long userId) {
		this.userDetails = userDetails;
		this.userId = userId;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return userDetails.getAuthorities();
	}

	@Override
	public String getPassword() {
		return userDetails.getPassword();
	}

	@Override
	public String getUsername() {
		return userDetails.getUsername();
	}

}
