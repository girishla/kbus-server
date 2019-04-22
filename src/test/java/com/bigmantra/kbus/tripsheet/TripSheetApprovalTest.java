package com.bigmantra.kbus.tripsheet;

import com.bigmantra.kbus.AbstractWebIntegrationTest;
import com.bigmantra.kbus.domain.BusDailySummary;
import com.bigmantra.kbus.security.SecurityTestApiConfig;
import com.bigmantra.kbus.security.UserObjectMother;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TripSheetApprovalTest extends AbstractWebIntegrationTest {


    private static boolean setUpIsDone = false;
    private static BusDailySummary busSummary;

    @Autowired
    private BusDailySummaryRepository repo;

    @Before
    public void setUp() throws Exception {

        if (setUpIsDone) {
            return;
        }

        busSummary=repo.save(BusDailySummary.builder()
                .single1Collection(new BigDecimal("1290"))
                .single2Collection(new BigDecimal("1440"))
                .dieselLitres(new BigDecimal("50"))
                .dieselExpense(new BigDecimal("4000"))
                .driverPathaExpense(new BigDecimal("150"))
                .driverSalaryAllowanceExpense(new BigDecimal("300"))
                .conductorPathaExpense(new BigDecimal("150"))
                .conductorSalaryAllowanceExpense(new BigDecimal("300"))
                .checkingPathaExpense(new BigDecimal("300"))
                .commissionExpense(new BigDecimal("140"))
                .otherExpense(new BigDecimal("200"))
                .unionExpense(new BigDecimal("150"))
                .cleanerExpense(new BigDecimal("150"))
                .build()

        );

        setUpIsDone = true;

    }

    @Test
    @WithUserDetails("girish")
    public void approvingTripSheetResurnsOK() throws Exception {

        MvcResult result = mvc.perform(patch("/tripsheet/approval")//
                .content(asJsonString(TripSheetApprovalRequest.builder().busSummaryId(busSummary.getId()).isApproved(true).build()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)//
                .accept(MediaType.APPLICATION_JSON_VALUE))//
                .andDo(MockMvcResultHandlers.print())//
                .andExpect(status().isOk())//
                .andExpect(jsonPath("$.approved", is(true)))//
                .andReturn();


    }


    @Test
    @WithUserDetails("girish")
    public void approvingTripSheetCreatesQBOSalesReceipt() throws Exception {

        MvcResult result = mvc.perform(patch("/tripsheet/approval")//
                .content(asJsonString(TripSheetApprovalRequest.builder().busSummaryId(busSummary.getId()).isApproved(true).build()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)//
                .accept(MediaType.APPLICATION_JSON_VALUE))//
                .andDo(MockMvcResultHandlers.print())//
                .andExpect(status().isOk())//
                .andExpect(jsonPath("$.approved", is(true)))//
                .andReturn();


        //Assert Sales Receipt Id saved into Bus Summary Table

        //Assert if QBO Sales Receipt Created Successfully



    }

}
