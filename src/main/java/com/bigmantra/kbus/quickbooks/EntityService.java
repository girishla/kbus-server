package com.bigmantra.kbus.quickbooks;

import com.intuit.ipp.core.IEntity;
import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.AuthenticationException;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.exception.InvalidTokenException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.ipp.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class EntityService {

    private static final String ACCOUNT_QUERY = "select * from Account where AccountType='%s' maxresults 1";
    private static final String CUSTOMER_QUERY = "select * from Customer where DisplayName='%s' maxresults 1";
    private static final String PAYMENT_METHOD_QUERY = "select * from PaymentMethod where name='Cheque' maxresults 1";
    private static final String ACCOUNT_EXP_QUERY = "select * from Account where AccountType='%s' and name='%s' maxresults 1";
    private static final String PRODUCT_QUERY = "select * from Item where name='%s' maxresults 1";


    @Autowired
    private TokenRefresher tokenRefresher;

    @Autowired
    private QBOServiceHelper helper;



    private Line getAccountBasedLine(DataService service, BigDecimal amount, AccountNameEnum accountNameEnum,CustomerNameEnum customerNameEnum) {
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


    private Line getSalesItemLine(DataService service, BigDecimal amount, ProductNameEnum productNameEnum) {
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

    private Line getItemBasedLine(DataService service, BigDecimal amount, ProductNameEnum productNameEnum,CustomerNameEnum customerNameEnum) {
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
    private PaymentMethod getPaymentMethodCash(DataService service)  {
        QueryResult queryResult = null;
        try {
            queryResult = service.executeQuery(String.format(PAYMENT_METHOD_QUERY));
        } catch (FMSException e) {
            tokenRefresher.refreshAccessToken();
            e.printStackTrace();
        }
        List<? extends IEntity> entities = queryResult.getEntities();
        if (!entities.isEmpty()) {
            return (PaymentMethod) entities.get(0);
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
    private Account getExpenseAccount(DataService service, AccountNameEnum accountNameEnum) {

        QueryResult queryResult = null;
        try {
            queryResult = service.executeQuery(String.format(ACCOUNT_EXP_QUERY, AccountTypeEnum.EXPENSE.value(), accountNameEnum.getAccountName()));
        } catch (FMSException e) {
            tokenRefresher.refreshAccessToken();
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
    private Item getProduct(DataService service, ProductNameEnum productNameEnum) {

        QueryResult queryResult = null;
        try {
            queryResult = service.executeQuery(String.format(PRODUCT_QUERY, productNameEnum.getProductName()));
        } catch (FMSException e) {
            tokenRefresher.refreshAccessToken();
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
            throw new RuntimeException("Could not find Customer!");
        }
    }



    /**
     * Get Bank Account
     *
     * @param service
     * @return
     * @throws FMSException
     */
    private Account getPaymentAccount(DataService service) {
        QueryResult queryResult = null;
        try {
            queryResult = service.executeQuery(String.format(ACCOUNT_QUERY, AccountTypeEnum.BANK.value()));
        } catch (FMSException e) {
            tokenRefresher.refreshAccessToken();

            throw new RuntimeException("Could not find Payment account!");
        }
        List<? extends IEntity> entities = queryResult.getEntities();
        if (!entities.isEmpty()) {
            return (Account) entities.get(0);
        } else {
            throw new RuntimeException("Could not find Payment account!");
        }

    }


    public SalesReceipt createSalesReceipt(Date receiptDate, CustomerNameEnum customerNameEnum, ProductNameEnum productNameEnum, BigDecimal amount) {


        try {
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
        } catch (InvalidTokenException | AuthenticationException e) {
            tokenRefresher.refreshAccessToken();
            log.error("Invalid Auth token...Calling refresh....Please rerun test");
            throw new RuntimeException("Invalid Auth token...Calling refresh....Please rerun test.", e);
        } catch (FMSException e) {
            e.printStackTrace();
            throw new RuntimeException("Error inserting Sales Receipt record.", e);
        }

    }


    public Purchase createExpense(List<ExpenseDTO> expenseDTOs) {

        try {
            DataService service = getDataService();
            Purchase purchase = new Purchase();
            purchase.setPaymentType(PaymentTypeEnum.CASH);
            purchase.setPaymentMethodRef(createRef(getPaymentMethodCash(service)));

            Account paymentAccount = getPaymentAccount(service);
            purchase.setAccountRef(createRef(paymentAccount));

            List<Line> lines=new ArrayList<>();

            expenseDTOs.forEach((dto)->{
                Line line;
                if (dto.getAccountNameEnum() != null) {
                    line = getAccountBasedLine(service, dto.getExpenseAmount(), dto.getAccountNameEnum(),dto.getCustomerNameEnum());
                } else {
                    if (dto.getProductNameEnum() != null) {
                        line = getItemBasedLine(service, dto.getExpenseAmount(), dto.getProductNameEnum(),dto.getCustomerNameEnum());
                    } else {
                        throw new IllegalArgumentException("Either Account Name or Product Name is required to be able to create an expense.");
                    }
                }

                lines.add(line);
            });

            purchase.setLine(lines);
            log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Successfully Added Expense!");

            return service.add(purchase);
        } catch (InvalidTokenException | AuthenticationException e) {

            tokenRefresher.refreshAccessToken();
            log.error("Invalid Auth token...Calling refresh....Please rerun ");
            throw new RuntimeException("Invalid Auth token...Calling refresh....Please rerun ", e);

        } catch (FMSException e) {
            e.printStackTrace();
            throw new RuntimeException("Error inserting Purchase record.", e);
        }


    }
}
