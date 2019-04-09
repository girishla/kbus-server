package com.bigmantra.kbus.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class UserObjectMother {

	public static User getNormalUserFor(String username, String password) {

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		return User.builder().username(username)//
				.password(passwordEncoder.encode(password))//
				.email(username +"@domain.com")//
				.authorities("USER")
				.build();

	}
	
	
	public static User getAdminUserFor(String username, String password) {

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		return User.builder().username(username)//
				.password(passwordEncoder.encode(password))//
				.authorities("USER, ADMIN, ROOT")
				.email(username +"@domain.com")//
				.build();

	}
	
	public static User getExpiredUserFor(String username, String password) throws ParseException {

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		DateFormat df = new SimpleDateFormat("dd/mm/yyyy");
		
		
		return User.builder().username(username)//
				.password(passwordEncoder.encode(password))//
				.lastPasswordReset(df.parse("01/01/2050"))
				.authorities("USER")
				.email(username +"@domain.com")//
				.build();

	}
	

}
