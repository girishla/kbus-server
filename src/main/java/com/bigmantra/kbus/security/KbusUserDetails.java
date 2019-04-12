package com.bigmantra.kbus.security;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
public class KbusUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 123278121L;
	private Long id;
	private String username;

	@JsonIgnore
	private String password;
	
	private String email;

	private String firstName;
	private String lastName;
	private String phone;


	@JsonIgnore
	private Date lastPasswordReset;


	private Collection<? extends GrantedAuthority> authorities;
	
	@JsonIgnore
	private Boolean accountNonExpired = true;

	@JsonIgnore
	private Boolean accountNonLocked = true;

	@JsonIgnore
	private Boolean credentialsNonExpired = true;

	private Boolean enabled = true;

	public KbusUserDetails() {
		super();
	}


	public KbusUserDetails(Long id, String username, String password,String firstName, String lastName, String email,String phone, Date lastPasswordReset,
						   Collection<? extends GrantedAuthority> authorities) {
		this.setId(id);
		this.setUsername(username);
		this.setPassword(password);
		this.setEmail(email);
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setPhone(phone);
		this.setLastPasswordReset(lastPasswordReset);
		this.setAuthorities(authorities);
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.getAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.getAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.getCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return this.getEnabled();
	}

}