package com.example.boot_jwt_db.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.boot_jwt_db.model.CustomUserDetails;

@RestController
public class InfoController {
	
	@GetMapping("/info")
	public String info() {
		Authentication authentication =
				SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null &&
				authentication.getPrincipal()
				instanceof CustomUserDetails) {
			CustomUserDetails user = (CustomUserDetails)
					authentication.getPrincipal();
			return "Username : " + user.getUsername()
				+ ", UserId : " + user.getUserId();
		}
		return "알 수 없음";
	}
}