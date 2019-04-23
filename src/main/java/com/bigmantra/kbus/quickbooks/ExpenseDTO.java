package com.bigmantra.kbus.quickbooks;

import com.bigmantra.kbus.tripsheet.BusDailySummary;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class ExpenseDTO {

    private CustomerNameEnum customerNameEnum;
    private AccountNameEnum accountNameEnum;
    private ProductNameEnum productNameEnum;
    private BigDecimal expenseAmount;
    private BigDecimal productQuantity;
    private String description;
    private Date expenseDate;


    public static List<ExpenseDTO> getExpensesFromTripSheet(BusDailySummary tripSheet) {

        List<ExpenseDTO> expenseDTOs = new ArrayList<>();

        expenseDTOs.add(ExpenseDTO.builder()
                .productNameEnum(ProductNameEnum.FUEL_DIESEL)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup()
                        .getName()))
                .expenseAmount(tripSheet.getDieselExpense())
                .productQuantity(tripSheet.getDieselLitres())
                .expenseDate(tripSheet.getSummaryDate())
                .description("Driver: " + tripSheet.getDriver()
                        .getFirstName() + " " + tripSheet.getDriver()
                        .getLastName())
                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.GREASE_EXPENSE)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup()
                        .getName()))
                .expenseAmount(tripSheet.getGreaseExpense())
                .expenseDate(tripSheet.getSummaryDate())

                .build());


        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.BUS_DRIVER_PATHA)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup()
                        .getName()))
                .expenseAmount(tripSheet.getDriverPathaExpense())
                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.BUS_DRIVER_SALARY_ALLOWANCE)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup()
                        .getName()))
                .expenseAmount(tripSheet.getDriverSalaryAllowanceExpense())
                .expenseDate(tripSheet.getSummaryDate())
                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.BUS_CONDUCTOR_PATHA)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup()
                        .getName()))
                .expenseAmount(tripSheet.getConductorPathaExpense())
                .expenseDate(tripSheet.getSummaryDate())

                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.BUS_CONDUCTOR_SALARY_ALLOWANCE)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup()
                        .getName()))
                .expenseAmount(tripSheet.getConductorSalaryAllowanceExpense())
                .expenseDate(tripSheet.getSummaryDate())

                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.BUS_CHECKING_PATHA)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup()
                        .getName()))
                .expenseAmount(tripSheet.getCheckingPathaExpense())
                .expenseDate(tripSheet.getSummaryDate())

                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.COMMISSION_CHARGES)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup()
                        .getName()))
                .expenseAmount(tripSheet.getCommissionExpense())
                .expenseDate(tripSheet.getSummaryDate())

                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.OTHER_COSTS_OF_SALES_COS)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup()
                        .getName()))
                .expenseAmount(tripSheet.getOtherExpense())
                .expenseDate(tripSheet.getSummaryDate())

                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.UNION_CHARGES)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup()
                        .getName()))
                .expenseAmount(tripSheet.getUnionExpense())
                .expenseDate(tripSheet.getSummaryDate())

                .build());


        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.BUS_CLEANER_WAGES)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup()
                        .getName()))
                .expenseAmount(tripSheet.getCleanerExpense())
                .expenseDate(tripSheet.getSummaryDate())

                .build());


        return expenseDTOs;

    }

}
