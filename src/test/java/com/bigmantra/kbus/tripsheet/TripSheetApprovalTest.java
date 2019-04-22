package com.bigmantra.kbus.tripsheet;

import com.bigmantra.kbus.AbstractWebIntegrationTest;
import com.bigmantra.kbus.domain.Group;
import com.bigmantra.kbus.domain.GroupRepository;
import com.bigmantra.kbus.security.User;
import com.bigmantra.kbus.security.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;


    @Before
    public void setUp() throws Exception {

        if (setUpIsDone) {
            return;
        }

        Date summaryDate=Date.from(LocalDate.parse("2019-04-01").atStartOfDay(ZoneId.systemDefault()).toInstant());

        User user=userRepository.save(User
                .builder()
                .username("test")
                .password("test1234")
                .firstName("test")
                .lastName("user")
                .email("test@user.com")
                .authorities("USER")
                .phone("+44 7545894530")
                .enabled(true)
                .build());



        busSummary=repo.save(BusDailySummary.builder()
                .single1Collection(new BigDecimal("1290"))
                .single2Collection(new BigDecimal("1440"))
                .single3Collection(new BigDecimal("0"))
                .single4Collection(new BigDecimal("0"))
                .single5Collection(new BigDecimal("0"))
                .single6Collection(new BigDecimal("0"))
                .single7Collection(new BigDecimal("0"))
                .single8Collection(new BigDecimal("0"))
                .single9Collection(new BigDecimal("0"))
                .single10Collection(new BigDecimal("0"))
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
                .summaryDate(summaryDate)
                .dateId(Long.valueOf("20190401"))
                .group(groupRepository.save(Group.builder()
                        .groupname("TN29BD3444")
                        .name("TN29BD3444")
                        .build()))
                .conductor(user)
                .driver(user)
                .submittedBy(user)
                .build()

        );

        setUpIsDone = true;

    }

    @Test
    @WithUserDetails("girish")
    public void approvingTripSheetResurnsOK() throws Exception {

        MvcResult result = mvc.perform(patch("/busdailysummaries/" + busSummary.getId() + "/approval")//
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

        MvcResult result = mvc.perform(patch("/busdailysummaries/" + busSummary.getId() + "/approval")//
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
