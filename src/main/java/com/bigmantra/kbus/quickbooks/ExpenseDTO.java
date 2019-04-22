package com.bigmantra.kbus.quickbooks;

import com.bigmantra.kbus.domain.BusDailySummary;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ExpenseDTO {

    private CustomerNameEnum customerNameEnum;
    private AccountNameEnum accountNameEnum;
    private ProductNameEnum productNameEnum;
    private BigDecimal expenseAmount;
    private BigDecimal productQuantity;


    public static List<ExpenseDTO> getExpensesFromTripSheet(BusDailySummary tripSheet){

        List<ExpenseDTO> expenseDTOs=new ArrayList<>();

        expenseDTOs.add(ExpenseDTO.builder()
                .productNameEnum(ProductNameEnum.FUEL_DIESEL)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup().getName()))
                .expenseAmount(tripSheet.getDieselExpense())
                .productQuantity(tripSheet.getDieselLitres())
                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.GREASE_EXPENSE)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup().getName()))
                .expenseAmount(tripSheet.getGreaseExpense())
                .build());


        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.BUS_DRIVER_PATHA)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup().getName()))
                .expenseAmount(tripSheet.getDriverPathaExpense())
                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.BUS_DRIVER_SALARY_ALLOWANCE)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup().getName()))
                .expenseAmount(tripSheet.getDriverSalaryAllowanceExpense())
                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.BUS_CONDUCTOR_PATHA)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup().getName()))
                .expenseAmount(tripSheet.getConductorPathaExpense())
                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.BUS_CONDUCTOR_SALARY_ALLOWANCE)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup().getName()))
                .expenseAmount(tripSheet.getConductorSalaryAllowanceExpense())
                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.BUS_CHECKING_PATHA)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup().getName()))
                .expenseAmount(tripSheet.getCheckingPathaExpense())
                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.COMMISSION_CHARGES)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup().getName()))
                .expenseAmount(tripSheet.getCommissionExpense())
                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.OTHER_COSTS_OF_SALES_COS)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup().getName()))
                .expenseAmount(tripSheet.getOtherExpense())
                .build());

        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.UNION_CHARGES)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup().getName()))
                .expenseAmount(tripSheet.getUnionExpense())
                .build());


        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.BUS_CLEANER_WAGES)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup().getName()))
                .expenseAmount(tripSheet.getCleanerExpense())
                .build());


        expenseDTOs.add(ExpenseDTO.builder()
                .accountNameEnum(AccountNameEnum.BUS_CHECKING_PATHA)
                .customerNameEnum(CustomerNameEnum.fromPlateName(tripSheet.getGroup().getName()))
                .expenseAmount(tripSheet.getCheckingPathaExpense())
                .build());


        return expenseDTOs;

    }

}
