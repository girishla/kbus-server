package com.bigmantra.kbus.quickbooks;

import com.intuit.ipp.core.IEntity;
import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.AuthenticationException;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.exception.InvalidTokenException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseService {

    private static final String ACCOUNT_QUERY = "select * from Account where AccountType='%s' maxresults 1";


    @Autowired
    private TokenRefresher tokenRefresher;

    @Autowired
    private QBOServiceHelper helper;

    public void createPurchase() {

        try {
            DataService service = getDataService();
            Purchase purchase = new Purchase();
            purchase.setPaymentType(PaymentTypeEnum.CASH);


            Account chequeQccount = getCheckBankAccount(service);
            purchase.setAccountRef(createRef(chequeQccount));

            Line line1 = new Line();
            line1.setAmount(new BigDecimal("30.00"));
            line1.setDetailType(LineDetailTypeEnum.ACCOUNT_BASED_EXPENSE_LINE_DETAIL);
            AccountBasedExpenseLineDetail detail = new AccountBasedExpenseLineDetail();
            Account expAccount = getExpenseBankAccount(service);
            ReferenceType expenseAccountRef = createRef(expAccount);
            detail.setAccountRef(expenseAccountRef);
            line1.setAccountBasedExpenseLineDetail(detail);


            List<Line> lines1 = new ArrayList<Line>();
            lines1.add(line1);
            purchase.setLine(lines1);
            Purchase purchaseOut = service.add(purchase);
        } catch (InvalidTokenException | AuthenticationException e) {

            tokenRefresher.refreshAccessToken();
        } catch (FMSException e) {
            e.printStackTrace();
            throw new RuntimeException("Error inserting Purchase record.", e);
        }


    }

    /**
     * Creates reference type for an entity
     *
     * @param entity - IntuitEntity object inherited by each entity
     * @return
     */
    private ReferenceType createRef(IntuitEntity entity) {
        ReferenceType referenceType = new ReferenceType();
        referenceType.setValue(entity.getId());
        return referenceType;
    }


    private DataService getDataService() {
        DataService service;//get DataService
        try {
            service = helper.getDataService(tokenRefresher.getCurrentRealmId(), tokenRefresher.getCurrentQuickbooksAccessToken());
        } catch (FMSException e) {
            throw new RuntimeException("Error getting Data Service", e);
        }
        return service;
    }

    /**
     * Get Bank Account
     *
     * @param service
     * @return
     * @throws FMSException
     */
    private Account getExpenseBankAccount(DataService service) {

        QueryResult queryResult = null;
        try {
            queryResult = service.executeQuery(String.format(ACCOUNT_QUERY, AccountTypeEnum.EXPENSE.value()));
        } catch (FMSException e) {
            e.printStackTrace();
        }
        List<? extends IEntity> entities = queryResult.getEntities();
        if (!entities.isEmpty()) {
            return (Account) entities.get(0);
        } else {
            throw new RuntimeException("Could not find Expense Bank account!");
        }
    }


    /**
     * Get Bank Account
     *
     * @param service
     * @return
     * @throws FMSException
     */
    private  Account getCheckBankAccount(DataService service) throws FMSException {
        QueryResult queryResult = service.executeQuery(String.format(ACCOUNT_QUERY, AccountTypeEnum.BANK.value()));
        List<? extends IEntity> entities = queryResult.getEntities();
        if(!entities.isEmpty()) {
            return (Account)entities.get(0);
        }
        else {
            throw new RuntimeException("Could not find Cheque Bank account!");
        }

    }


}
