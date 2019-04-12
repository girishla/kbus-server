package com.bigmantra.kbus.domain;

import com.bigmantra.kbus.security.User;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "expenseProjection", types = {Expense.class})
public interface ExpenseProjection {

    String getId();

    Group getGroup();

    User getUser();

    Category getCategory();

    String getPhotos();

    String getNote();

    double getAmount();

    Date getExpenseDate();
    Date getCreatedDate();


}


