package com.bigmantra.kbus.tripsheet;

import com.bigmantra.kbus.domain.AbstractKbusObject;
import com.bigmantra.kbus.domain.Group;
import com.bigmantra.kbus.security.User;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "busdailysummary")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor

public class BusDailySummary extends AbstractKbusObject {

	// date stored as yyyymmdd
	Long dateId;

	private Date summaryDate;


	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "groupid")
	Group group;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "driverid")
	User driver;

	@JsonValue
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
	private BigDecimal dieselLitres;
	private BigDecimal greaseExpense;
	private BigDecimal driverPathaExpense;
	private BigDecimal driverSalaryAllowanceExpense;
	private BigDecimal conductorPathaExpense;
	private BigDecimal conductorSalaryAllowanceExpense;
	private BigDecimal checkingPathaExpense;
	private BigDecimal commissionExpense;
	private BigDecimal otherExpense;
	private BigDecimal unionExpense;
	private BigDecimal cleanerExpense;


	private String salesReceiptId;
	private String expenseId;


	public BigDecimal getTotalCollection(){
		return getSingle1Collection()
				.add(getSingle2Collection())
				.add(getSingle3Collection())
				.add(getSingle4Collection())
				.add(getSingle5Collection())
				.add(getSingle6Collection())
				.add(getSingle7Collection())
				.add(getSingle8Collection())
				.add(getSingle9Collection())
				.add(getSingle10Collection());

	}

}