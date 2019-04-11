package com.bigmantra.kbus.domain;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "usergroup")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Group extends AbstractKbusObject {

	@NonNull
	@NotBlank(message = "name cannot be empty!")
	private String name;

	private String groupname;

	private String about;

//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "userid",nullable = false)
//	User user;

	long userId;

	private BigDecimal weeklyBudget;
	private BigDecimal monthlyBudget;


}