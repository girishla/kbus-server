package com.bigmantra.kbus.domain;

import com.bigmantra.kbus.security.User;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "expense")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Expense extends AbstractKbusObject {

	private String photos;
	private String note;
	private double amount;
	private Date expenseDate;
	private boolean isSynced;



	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "categoryId")
	Category category;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "groupid")
	Group group;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userid")
	User user;
}