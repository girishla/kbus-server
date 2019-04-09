package com.bigmantra.kbus.security;

import java.util.Date;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

import com.bigmantra.kbus.domain.AbstractKbusObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor

public class User extends AbstractKbusObject {

	@NonNull
	@NotBlank(message = "user name cannot be empty!")
	private String username;

	@NonNull
	@NotBlank(message = "password cannot be empty!")
	private String password;
	
	private String email;

	private boolean enabled=true;
	
	private Date lastPasswordReset;
	private String authorities;

}