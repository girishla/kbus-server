package com.bigmantra.kbus.quickbooks;

import com.intuit.ipp.core.IEntity;
import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.AuthenticationException;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.exception.InvalidTokenException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ExpenseService {

    private static final String ACCOUNT_QUERY = "select * from Account where AccountType='%s' maxresults 1";
    private static final String PAYMENT_METHOD_QUERY = "select * from PaymentMethod where name='Cheque' maxresults 1";
    private static final String ACCOUNT_EXP_QUERY = "select * from Account where AccountType='%s' and name='%s' maxresults 1";
    private static final String PRODUCT_QUERY = "select * from Item where name='Parker Pen' maxresults 1";


    @Autowired
    private TokenRefresher tokenRefresher;

    @Autowired
    private QBOServiceHelper helper;

    public void createExpense(CustomerNameEnum customerNameEnum, AccountNameEnum accountNameEnum, ProductNameEnum productNameEnum, BigDecimal expenseAmount) {

        try {
            DataService service = getDataService();
            Purchase purchase = new Purchase();
            purchase.setPaymentType(PaymentTypeEnum.CASH);
            purchase.setPaymentMethodRef(createRef(getPaymentMethodCash(service)));

            Account paymentAccount = getPaymentAccount(service);
            purchase.setAccountRef(createRef(paymentAccount));
            Line line;
            if(accountNameEnum!=null){
                 line = getAccountBasedLine(service,expenseAmount);

            }else{
                if(productNameEnum!=null){
                    line = getItemBasedLine(service,expenseAmount);
                }else{
                    throw new IllegalArgumentException("Either Account Name or Product Name is required to be able to creeate an expense.");
                }
            }

            purchase.setLine(Arrays.asList(line));
            Purchase purchaseOut = service.add(purchase);
            log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Successfully Added Purchase!");
        } catch (InvalidTokenException | AuthenticationException e) {

            tokenRefresher.refreshAccessToken();
            log.error("Invalid Auth token...Calling refresh....Please rerun test");
        } catch (FMSException e) {
            e.printStackTrace();
            throw new RuntimeException("Error inserting Purchase record.", e);
        }


    }

    private Line getAccountBasedLine(DataService service,BigDecimal amount) {
        Line line1 = new Line();
        line1.setAmount(amount);
        line1.setDetailType(LineDetailTypeEnum.ACCOUNT_BASED_EXPENSE_LINE_DETAIL);
        AccountBasedExpenseLineDetail detail = new AccountBasedExpenseLineDetail();
        Account expAccount = getExpenseAccount(service);
        ReferenceType expenseAccountRef = createRef(expAccount);
        detail.setAccountRef(expenseAccountRef);
        line1.setAccountBasedExpenseLineDetail(detail);
        return line1;
    }

    private Line getItemBasedLine(DataService service,BigDecimal amount) {
        Line line2 = new Line();
        line2.setAmount(amount);
        line2.setDetailType(LineDetailTypeEnum.ITEM_BASED_EXPENSE_LINE_DETAIL);
        ItemBasedExpenseLineDetail itemBasedExpenseLineDetail = new ItemBasedExpenseLineDetail();
        Item item = getProduct(service);
        ReferenceType itemRef = createRef(item);
        itemBasedExpenseLineDetail.setItemRef(itemRef);
        itemBasedExpenseLineDetail.setQty(new BigDecimal("1"));
//            itemBasedExpenseLineDetail.setCustomerRef();
        line2.setItemBasedExpenseLineDetail(itemBasedExpenseLineDetail);
        return line2;
    }


    /**
     * Get Payment Method
     *
     * @param service
     * @return
     * @throws FMSException
     */
    private  PaymentMethod getPaymentMethodCash(DataService service) throws FMSException {
        QueryResult queryResult = service.executeQuery(String.format(PAYMENT_METHOD_QUERY));
        List<? extends IEntity> entities = queryResult.getEntities();
        if(!entities.isEmpty()) {
            return (PaymentMethod)entities.get(0);
        } else {
            throw new RuntimeException("Could not find Expense Bank account!");
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
    private Account getExpenseAccount(DataService service) {

        QueryResult queryResult = null;
        try {
            queryResult = service.executeQuery(String.format(ACCOUNT_EXP_QUERY,AccountTypeEnum.EXPENSE.value(),"Uncategorised Expense"));
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
     * Get Product
     *
     * @param service
     * @return
     * @throws FMSException
     */
    private Item getProduct(DataService service) {

        QueryResult queryResult = null;
        try {
            queryResult = service.executeQuery(String.format(PRODUCT_QUERY));
        } catch (FMSException e) {
            e.printStackTrace();
        }
        List<? extends IEntity> entities = queryResult.getEntities();
        if (!entities.isEmpty()) {
            return (Item) entities.get(0);
        } else {
            throw new RuntimeException("Could not find Product!");
        }
    }


    /**
     * Get Bank Account
     *
     * @param service
     * @return
     * @throws FMSException
     */
    private  Account getPaymentAccount(DataService service) {
        QueryResult queryResult = null;
        try {
            queryResult = service.executeQuery(String.format(ACCOUNT_QUERY, AccountTypeEnum.BANK.value()));
        } catch (FMSException e) {
            throw new RuntimeException("Could not find Payee Bank account!");
        }
        List<? extends IEntity> entities = queryResult.getEntities();
        if(!entities.isEmpty()) {
            return (Account)entities.get(0);
        }
        else {
            throw new RuntimeException("Could not find Cheque Bank account!");
        }

    }


}
