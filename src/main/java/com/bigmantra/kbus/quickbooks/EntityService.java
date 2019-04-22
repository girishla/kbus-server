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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EntityService {

    private static final String ACCOUNT_QUERY = "select * from Account where AccountType='%s' maxresults 1";
    private static final String CUSTOMER_QUERY = "select * from Customer where DisplayName='%s' maxresults 1";
    private static final String PAYMENT_METHOD_QUERY = "select * from PaymentMethod where name='Cheque' maxresults 1";
    private static final String ACCOUNT_EXP_QUERY = "select * from Account where name='%s' maxresults 1";
    private static final String PRODUCT_QUERY = "select * from Item where name='%s' maxresults 1";

    @Autowired
    private TokenRefresher tokenRefresher;

    @Autowired
    private QBOServiceHelper helper;


    private Line getAccountBasedLine(DataService service, BigDecimal amount, AccountNameEnum accountNameEnum, CustomerNameEnum customerNameEnum) throws FMSException {
        Line line1 = new Line();
        line1.setAmount(amount);
        line1.setDetailType(LineDetailTypeEnum.ACCOUNT_BASED_EXPENSE_LINE_DETAIL);
        AccountBasedExpenseLineDetail detail = new AccountBasedExpenseLineDetail();
        Account expAccount = getExpenseAccount(service, accountNameEnum);
        ReferenceType expenseAccountRef = createRef(expAccount);
        detail.setAccountRef(expenseAccountRef);
        detail.setCustomerRef(createRef(getCustomer(service, customerNameEnum)));
        line1.setAccountBasedExpenseLineDetail(detail);
        return line1;
    }


    private Line getSalesItemLine(DataService service, BigDecimal amount, ProductNameEnum productNameEnum) throws FMSException {
        Line line1 = new Line();
        line1.setAmount(amount);
        line1.setDetailType(LineDetailTypeEnum.SALES_ITEM_LINE_DETAIL);
        line1.setLineNum(new BigInteger("1"));
        line1.setAmount(amount);
        line1.setDetailType(LineDetailTypeEnum.SALES_ITEM_LINE_DETAIL);

        SalesItemLineDetail salesItemLineDetail1 = new SalesItemLineDetail();
        Item item = getProduct(service, productNameEnum);
        ReferenceType itemRef = createRef(item);
        salesItemLineDetail1.setItemRef(itemRef);

        salesItemLineDetail1.setUnitPrice(amount);
        salesItemLineDetail1.setQty(new BigDecimal(1));
        line1.setSalesItemLineDetail(salesItemLineDetail1);

        line1.setSalesItemLineDetail(salesItemLineDetail1);
        return line1;
    }

    private Line getItemBasedLine(DataService service, BigDecimal amount, ProductNameEnum productNameEnum, CustomerNameEnum customerNameEnum) throws FMSException {
        Line line2 = new Line();
        line2.setAmount(amount);
        line2.setDetailType(LineDetailTypeEnum.ITEM_BASED_EXPENSE_LINE_DETAIL);
        ItemBasedExpenseLineDetail itemBasedExpenseLineDetail = new ItemBasedExpenseLineDetail();
        Item item = getProduct(service, productNameEnum);
        ReferenceType itemRef = createRef(item);
        itemBasedExpenseLineDetail.setItemRef(itemRef);
        itemBasedExpenseLineDetail.setQty(new BigDecimal("1"));
        itemBasedExpenseLineDetail.setCustomerRef(createRef(getCustomer(service, customerNameEnum)));
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

    @Retryable(
            value = FMSException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 1000))
    private PaymentMethod getPaymentMethodCash(DataService service) throws FMSException {
        QueryResult queryResult = null;
        queryResult = service.executeQuery(String.format(PAYMENT_METHOD_QUERY));

        List<? extends IEntity> entities = queryResult.getEntities();
        if (!entities.isEmpty()) {
            return (PaymentMethod) entities.get(0);
        } else {
            throw new RuntimeException("Could not find Expense Bank account!");
        }
    }


    /**
     * Refresh token and retry
     *
     * @param e
     */

    @Recover
    public PaymentMethod handleFMSException_getPaymentMethodCash(FMSException e,DataService service) {
        tokenRefresher.refreshAccessToken();
        return null;
    }

    @Recover
    public DataService handleFMSException_getDataService(FMSException e,DataService service) {
        tokenRefresher.refreshAccessToken();
        return null;
    }

    @Recover
    public Account handleFMSException_getExpenseAccount(FMSException e,DataService service) {
        tokenRefresher.refreshAccessToken();
        return null;
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


    @Retryable(
            value = FMSException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 1000))
    private DataService getDataService() throws FMSException {
        DataService service;//get DataService
        service = helper.getDataService(tokenRefresher.getCurrentRealmId(), tokenRefresher.getCurrentQuickbooksAccessToken());

        return service;
    }

    /**
     * Get Bank Account
     *
     * @param service
     * @return
     * @throws FMSException
     */
    @Retryable(
            value = FMSException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 1000))
    private Account getExpenseAccount(DataService service, AccountNameEnum accountNameEnum) throws FMSException {

        QueryResult queryResult = null;
        queryResult = service.executeQuery(String.format(ACCOUNT_EXP_QUERY, accountNameEnum.getAccountName()));

        List<? extends IEntity> entities = queryResult.getEntities();
        if (!entities.isEmpty()) {
            return (Account) entities.get(0);
        } else {
            throw new RuntimeException("Could not find Expense account! " + accountNameEnum.getAccountName());
        }
    }


    /**
     * Get Product
     *
     * @param service
     * @return
     * @throws FMSException
     */
    @Retryable(
            value = {FMSException.class},
            maxAttempts = 2,
            backoff = @Backoff(delay = 1000))
    public Item getProduct(DataService service, ProductNameEnum productNameEnum) throws FMSException {

        QueryResult queryResult = null;
        queryResult = service.executeQuery(String.format(PRODUCT_QUERY, productNameEnum.getProductName()));

        List<? extends IEntity> entities = queryResult.getEntities();
        if (!entities.isEmpty()) {
            return (Item) entities.get(0);
        } else {
            throw new RuntimeException("Could not find Product!" + productNameEnum.getProductName());
        }
    }

    @Recover
    public Item handleFMSException_getProduct(FMSException e,DataService service, ProductNameEnum productNameEnum) {
        tokenRefresher.refreshAccessToken();
        return null;
    }

    @Recover
    public SalesReceipt handleFMSException_createSalesReceipt(FMSException e,Date receiptDate, CustomerNameEnum customerNameEnum, ProductNameEnum productNameEnum, BigDecimal amount) {
        tokenRefresher.refreshAccessToken();
        return null;
    }



    /**
     * Get Customer
     *
     * @param service
     * @return
     * @throws
     */
    private Customer getCustomer(DataService service, CustomerNameEnum customerNameEnum) {

        QueryResult queryResult = null;
        try {
            queryResult = service.executeQuery(String.format(CUSTOMER_QUERY, customerNameEnum.getCustomerName()));
        } catch (FMSException e) {
            tokenRefresher.refreshAccessToken();

            e.printStackTrace();
        }
        List<? extends IEntity> entities = queryResult.getEntities();
        if (!entities.isEmpty()) {
            return (Customer) entities.get(0);
        } else {
            throw new RuntimeException("Could not find Customer! " + customerNameEnum.getCustomerName());
        }
    }


    /**
     * Get Bank Account
     *
     * @param service
     * @return
     * @throws FMSException
     */
    @Retryable(
            value = FMSException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 1000))
    private Account getPaymentAccount(DataService service) throws FMSException {
        QueryResult queryResult = null;
        queryResult = service.executeQuery(String.format(ACCOUNT_QUERY, AccountTypeEnum.BANK.value()));

        List<? extends IEntity> entities = queryResult.getEntities();
        if (!entities.isEmpty()) {
            return (Account) entities.get(0);
        } else {
            throw new RuntimeException("Could not find Payment account!");
        }

    }


    @Retryable(
            value = FMSException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 1000))
    public SalesReceipt createSalesReceipt(Date receiptDate, CustomerNameEnum customerNameEnum, ProductNameEnum productNameEnum, BigDecimal amount) throws FMSException {

        DataService service = getDataService();
        SalesReceipt salesReceipt = new SalesReceipt();
        salesReceipt.setTxnDate(receiptDate);
        Line line1 = getSalesItemLine(service, amount, productNameEnum);
        salesReceipt.setLine(Arrays.asList(line1));
        salesReceipt.setDepositToAccountRef(createRef(getPaymentAccount(service)));
        salesReceipt.setCustomerRef(createRef(getCustomer(service, customerNameEnum)));
        salesReceipt.setApplyTaxAfterDiscount(false);
        salesReceipt.setTotalAmt(amount);
        salesReceipt.setGlobalTaxCalculation(GlobalTaxCalculationEnum.NOT_APPLICABLE);
        return service.add(salesReceipt);

    }


    public Purchase createExpense(List<ExpenseDTO> expenseDTOs) throws FMSException {

        DataService service = getDataService();
        Purchase purchase = new Purchase();
        purchase.setPaymentType(PaymentTypeEnum.CASH);
        purchase.setPaymentMethodRef(createRef(getPaymentMethodCash(service)));

        Account paymentAccount = getPaymentAccount(service);
        purchase.setAccountRef(createRef(paymentAccount));

        List<Line> lines = new ArrayList<>();

        expenseDTOs.forEach((dto) -> {
            Line line;

            try {
                if (dto.getAccountNameEnum() != null) {

                    line = getAccountBasedLine(service, dto.getExpenseAmount(), dto.getAccountNameEnum(), dto.getCustomerNameEnum());

                } else {
                    if (dto.getProductNameEnum() != null) {
                        line = getItemBasedLine(service, dto.getExpenseAmount(), dto.getProductNameEnum(), dto.getCustomerNameEnum());
                    } else {
                        throw new IllegalArgumentException("Either Account Name or Product Name is required to be able to create an expense.");
                    }
                }
            } catch (FMSException e) {
                throw new RuntimeException("Error getting Account based line!", e);
            }
            lines.add(line);
        });


        lines.forEach((line)->log.info(" Amount is for :" + line.getDetailType() + ":::" + line.getAmount()));


        purchase.setLine(lines
                .stream()
                .filter(line->line.getAmount()!=null).collect(Collectors.toList()));

        return service.add(purchase);


    }
}
