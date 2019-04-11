package com.bigmantra.kbus.security;

import com.bigmantra.kbus.domain.AbstractKbusObject;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

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

	private String firstName;
	private String lastName;

	@NonNull
	@NotBlank(message = "password cannot be empty!")
	private String password;
	
	private String email;

	private boolean enabled=true;
	
	private Date lastPasswordReset;
	private String authorities;


//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<Group> ownedGroups = new HashSet<>();


}