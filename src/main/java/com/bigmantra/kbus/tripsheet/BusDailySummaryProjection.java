package com.bigmantra.kbus.tripsheet;

import com.bigmantra.kbus.domain.Group;
import com.bigmantra.kbus.security.User;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;
import java.util.Date;

@Projection(name = "busdailysummaryProjection", types = {BusDailySummary.class})
public interface BusDailySummaryProjection {

    String getId();
    Long getDateId();
    User getDriver();
    User getConductor();
    Group getGroup();
    User getSubmittedBy();
    boolean isApproved();
    Date getCreatedDate();
    Date getSummaryDate();
    BigDecimal getSingle1Collection();

    BigDecimal getSingle2Collection();

    BigDecimal getSingle3Collection();

    BigDecimal getSingle4Collection();

    BigDecimal getSingle5Collection();

    BigDecimal getSingle6Collection();

    BigDecimal getSingle7Collection();

    BigDecimal getSingle8Collection();

    BigDecimal getSingle9Collection();

    BigDecimal getSingle10Collection();

    BigDecimal getDieselExpense();

    BigDecimal getdieselLitres();

    BigDecimal getgreaseExpense();

    BigDecimal getDriverPathaExpense();

    BigDecimal getDriverSalaryAllowanceExpense();

    BigDecimal getConductorPathaExpense();

    BigDecimal getConductorSalaryAllowanceExpense();

    BigDecimal getCheckingPathaExpense();

    BigDecimal getCommissionExpense();

    BigDecimal getOtherExpense();

    BigDecimal getUnionExpense();

    BigDecimal getCleanerExpense();
    String getSalesReceiptId();
    String getExpenseId();
}


