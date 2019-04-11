package com.bigmantra.kbus.domain;

import com.bigmantra.kbus.security.User;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "busdailysummary")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BusDailySummary extends AbstractKbusObject {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "groupid")
	Group group;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "driverid")
	User driver;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "conductorid")
	User conductor;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "submittedbyid")
	User submittedBy;

	private boolean isApproved = false;

	private BigDecimal single1Collection;
	private BigDecimal single2Collection;
	private BigDecimal single3Collection;
	private BigDecimal single4Collection;
	private BigDecimal single5Collection;
	private BigDecimal single6Collection;
	private BigDecimal single7Collection;
	private BigDecimal single8Collection;
	private BigDecimal single9Collection;
	private BigDecimal single10Collection;

	private BigDecimal dieselExpense;
	private BigDecimal oilExpense;
	private BigDecimal waterExpense;
	private BigDecimal driverPathaExpense;
	private BigDecimal driverSalaryAllowanceExpense;
	private BigDecimal conductorPathaExpense;
	private BigDecimal conductorSalaryAllowanceExpense;
	private BigDecimal checkingPathaExpense;
	private BigDecimal commissionExpense;
	private BigDecimal otherExpense;
	private BigDecimal unionExpense;
	private BigDecimal cleanerExpense;



}