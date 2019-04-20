package com.bigmantra.kbus.quickbooks;

import lombok.Getter;

public enum AccountNameEnum {


    BUS_DRIVER_PATHA("Bus Driver Patha", "Bus Driver Patha"),
    BUS_DRIVER_SALARY_ALLOWANCE("Bus Driver Salary Allowance", "Bus Driver Salary Allowance"),
    BUS_CONDUCTOR_PATHA("Bus Conductor Patha", "Bus Conductor Patha"),
    BUS_CONDUCTOR_SALARY_ALLOWANCE("Bus Conductor Salary Allowance", "Bus Conductor Salary Allowance"),
    BUS_CHECKING_PATHA("Bus Checking Patha", "Bus Checking Patha"),
    COMMISSION_CHARGES("Commission charges", "Commission charges"),
    UNION_CHARGES("Union charges", "Union charges"),
    BUS_CLEANER_WAGES("Bus Cleaner wages", "Bus Cleaner wages"),
    OTHER_COSTS_OF_SALES_COS("Other costs of sales - COS", "Other costs of sales - COS"),
    GREASE_EXPENSE("Grease expense", "Grease expense"),
    UNCATEGORISED_EXPENSE("Uncategorised Expense", "Uncategorised Expense");

    @Getter
    private String accountName;
    @Getter
    private String categoryName;

    AccountNameEnum(String accountName, String categoryName) {
        this.accountName = accountName;
        this.categoryName = categoryName;
    }


    public static AccountNameEnum fromCategoryName(String categoryName) {
        for (AccountNameEnum b : AccountNameEnum.values()) {
            if (b.categoryName.equalsIgnoreCase(categoryName)) {
                return b;
            }
        }
        return null;
    }

    public static AccountNameEnum fromAccountName(String accountName) {
        for (AccountNameEnum b : AccountNameEnum.values()) {
            if (b.accountName.equalsIgnoreCase(accountName)) {
                return b;
            }
        }
        return null;
    }


}
