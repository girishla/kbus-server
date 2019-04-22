package com.bigmantra.kbus.tripsheet;

import com.bigmantra.kbus.quickbooks.CustomerNameEnum;
import com.bigmantra.kbus.quickbooks.EntityService;
import com.bigmantra.kbus.quickbooks.ExpenseDTO;
import com.bigmantra.kbus.quickbooks.ProductNameEnum;
import com.intuit.ipp.data.Purchase;
import com.intuit.ipp.data.SalesReceipt;
import com.intuit.ipp.exception.FMSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("busdailysummaries/{busSummaryId}/approval")
public class TripSheetApprovalController {

    @Autowired
    private BusDailySummaryRepository repo;

    @Autowired
    private EntityService entityService;

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<?> authenticationRequest(@RequestBody TripSheetApprovalRequest tripSheetApprovalRequest) throws FMSException {

        BusDailySummary tripSheet = repo.findById(tripSheetApprovalRequest.getBusSummaryId())
                .orElseThrow(() -> new IllegalArgumentException("Could not find Trip sheet. Please verify Trip sheet id"));


        if(!(tripSheet.getSalesReceiptId()==null)){
            throw new IllegalStateException("This Trip Sheet already has a Sales Receipt associated. Aborting approval!");
        }

        if(!(tripSheet.getExpenseId()==null)){
            throw new IllegalStateException("This Trip Sheet already has a Expense record associated. Aborting approval!");
        }

        tripSheet.setApproved(tripSheetApprovalRequest.isApproved());


        SalesReceipt salesReceipt=entityService.createSalesReceipt(tripSheet.getSummaryDate(),
                CustomerNameEnum.fromPlateName(tripSheet.getGroup().getName()),
                ProductNameEnum.fromCategoryName(tripSheet.getGroup().getName()),
                tripSheet.getTotalCollection());

        Purchase purchase=entityService.createExpense(ExpenseDTO.getExpensesFromTripSheet(tripSheet));

        tripSheet.setSalesReceiptId(salesReceipt.getId());
        tripSheet.setExpenseId(purchase.getId());
        repo.save(tripSheet);

        return ResponseEntity.ok(TripSheetApprovalResponse.builder()
                .busSummaryId(tripSheetApprovalRequest.getBusSummaryId())
                .salesReceiptId(salesReceipt.getId())
                .expenseId((purchase.getId()))
                .isApproved(tripSheetApprovalRequest.isApproved()).build());

    }

}
