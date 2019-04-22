package com.bigmantra.kbus.quickbooks;

import com.intuit.ipp.exception.FMSException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EntityServiceTest {

    @Autowired
    private EntityService entityService;

    @Test
    public void canCreateExpenseRecord() throws FMSException {

//        entityService.createExpense(CustomerNameEnum.DHARMAPURI_SALEM, AccountNameEnum.UNCATEGORISED_EXPENSE, null, new BigDecimal("139.0"));
        entityService.createExpense(Arrays.asList(ExpenseDTO
                .builder()
                .customerNameEnum(CustomerNameEnum.DHARMAPURI_SALEM)
                .accountNameEnum(AccountNameEnum.UNCATEGORISED_EXPENSE)
                .productNameEnum(null)
                .expenseAmount( new BigDecimal("139.0"))
                .build()));

    }


    @Test
    public void canCreateSalesReceiptRecord() throws Exception {

        entityService.createSalesReceipt(new Date(), CustomerNameEnum.DHARMAPURI_SALEM, ProductNameEnum.DHARMAPURI_SALEM, new BigDecimal("11111.0"));

    }



}
