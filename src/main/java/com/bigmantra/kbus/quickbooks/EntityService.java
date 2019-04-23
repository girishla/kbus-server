package com.bigmantra.kbus.quickbooks;

import com.intuit.ipp.core.IEntity;
import com.intuit.ipp.data.*;
import com.intuit.ipp.exception.AuthenticationException;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.exception.InvalidTokenException;
import com.intuit.ipp.query.GenerateQuery;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.intuit.ipp.query.GenerateQuery.$;
import static com.intuit.ipp.query.GenerateQuery.select;

@Service
@Slf4j
public class EntityService {

    private static final String ACCOUNT_QUERY = "select * from Account where AccountType='%s' maxresults 1";
    private static final String ACCOUNT_QUERY_IN = "select * from Account where Name in %s";
    private static final String CUSTOMER_QUERY = "select * from Customer where DisplayName='%s' maxresults 1";
    private static final String CUSTOMER_QUERY_IN = "select * from Customer where DisplayName in %s";
    private static final String PAYMENT_METHOD_QUERY = "select * from PaymentMethod where name='Cheque' maxresults 1";
    private static final String PAYMENT_METHOD_IN = "select * from PaymentMethod where name in %s";
    private static final String ACCOUNT_EXP_QUERY = "select * from Account where name='%s' maxresults 1";
    private static final String PRODUCT_QUERY = "select * from Item where name='%s' maxresults 1";
    private static final String PRODUCT_QUERY_IN = "select * from Item where name in %s";

    @Autowired
    private TokenRefresher tokenRefresher;

    @Autowired
    private QBOServiceHelper helper;

    private List<Account> accounts;
    private List<Customer> customers;
    private List<Item> products;
    private List<PaymentMethod> paymentMethods;


    private Line getAccountBasedLine(DataService service, BigDecimal amount, AccountNameEnum accountNameEnum, CustomerNameEnum customerNameEnum, String description) throws FMSException {
        Line line1 = new Line();
        line1.setAmount(amount);
        line1.setDetailType(LineDetailTypeEnum.ACCOUNT_BASED_EXPENSE_LINE_DETAIL);
        AccountBasedExpenseLineDetail detail = new AccountBasedExpenseLineDetail();
        Account expAccount = getExpenseAccount(accountNameEnum);
        ReferenceType expenseAccountRef = createRef(expAccount);
        detail.setAccountRef(expenseAccountRef);
        detail.setCustomerRef(createRef(getCustomer(customerNameEnum)));
        line1.setAccountBasedExpenseLineDetail(detail);
        if (description != null) {
            line1.setDescription(description);

        }
        return line1;
    }


    private Line getSalesItemLine(DataService service, BigDecimal amount, ProductNameEnum productNameEnum, Date receiptDate) throws FMSException {
        Line line1 = new Line();
        line1.setAmount(amount);
        line1.setDetailType(LineDetailTypeEnum.SALES_ITEM_LINE_DETAIL);
        line1.setLineNum(new BigInteger("1"));
        line1.setAmount(amount);
        line1.setDetailType(LineDetailTypeEnum.SALES_ITEM_LINE_DETAIL);

        SalesItemLineDetail salesItemLineDetail1 = new SalesItemLineDetail();
        Item item = getProduct(productNameEnum);
        ReferenceType itemRef = createRef(item);
        salesItemLineDetail1.setItemRef(itemRef);

        salesItemLineDetail1.setUnitPrice(amount);
        salesItemLineDetail1.setQty(new BigDecimal(1));
        salesItemLineDetail1.setServiceDate(receiptDate);
        line1.setSalesItemLineDetail(salesItemLineDetail1);

        line1.setSalesItemLineDetail(salesItemLineDetail1);
        return line1;
    }

    private Line getItemBasedLine(DataService service, BigDecimal amount, ProductNameEnum productNameEnum, CustomerNameEnum customerNameEnum, String description) throws FMSException {
        Line line2 = new Line();
        line2.setAmount(amount);
        line2.setDetailType(LineDetailTypeEnum.ITEM_BASED_EXPENSE_LINE_DETAIL);
        ItemBasedExpenseLineDetail itemBasedExpenseLineDetail = new ItemBasedExpenseLineDetail();
        Item item = getProduct(productNameEnum);
        ReferenceType itemRef = createRef(item);
        itemBasedExpenseLineDetail.setItemRef(itemRef);
        itemBasedExpenseLineDetail.setQty(new BigDecimal("1"));
        itemBasedExpenseLineDetail.setCustomerRef(createRef(getCustomer(customerNameEnum)));
        line2.setItemBasedExpenseLineDetail(itemBasedExpenseLineDetail);
        if (description != null) {
            line2.setDescription(description);

        }
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

    private Account getExpenseAccount(AccountNameEnum accountNameEnum) throws FMSException {

        return accounts.stream()
                .filter(a -> a.getName()
                        .equals(accountNameEnum.getAccountName()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Could not find Account!" + accountNameEnum.getAccountName()));

    }


    public Item getProduct(ProductNameEnum productNameEnum) {


        return products.stream()
                .filter(p -> p.getName()
                        .equals(productNameEnum.getProductName()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Could not find Product!" + productNameEnum.getProductName()));

    }


    /**
     * Get Payment Account
     *
     * @return
     * @throws FMSException
     */

    private Account getPaymentAccount() throws FMSException {
        return accounts.stream()
                .filter(a -> a.getName()
                        .equals(AccountNameEnum.CASH_ON_HAND.getAccountName()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Could not find Account!" + AccountNameEnum.CASH_ON_HAND.getAccountName()));

    }

    /**
     * Get Customer
     *
     * @param customerNameEnum
     * @return
     * @throws
     */
    private Customer getCustomer(CustomerNameEnum customerNameEnum) {
        return customers.stream()
                .filter(c -> c.getDisplayName()
                        .equals(customerNameEnum.getCustomerName()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Could not find Customer!" + customerNameEnum.getCustomerName()));

    }

    /**
     * Get Customer
     *
     * @param paymentMethodEnum
     * @return
     * @throws
     */
    private PaymentMethod getPaymentMethod(PaymentMethodEnum paymentMethodEnum) {
        return paymentMethods.stream()
                .filter(c -> c.getName()
                        .equals("Cheque"))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Could not find Payment Method Cheque"));

    }


    @Recover
    public Item handleFMSException_getProduct(FMSException e, DataService service, ProductNameEnum productNameEnum) {
        tokenRefresher.refreshAccessToken();
        return null;
    }

    @Recover
    public SalesReceipt handleFMSException_createSalesReceipt(FMSException e, Date receiptDate, CustomerNameEnum customerNameEnum, ProductNameEnum productNameEnum, BigDecimal amount) {
        tokenRefresher.refreshAccessToken();
        e.printStackTrace();
        return null;
    }

    @Recover
    public DataService handleFMSException_getDataService(FMSException e, DataService service) {
        tokenRefresher.refreshAccessToken();
        return null;
    }

    @Recover
    public void handleFMSException(FMSException e, DataService service) {
        e.printStackTrace();
        tokenRefresher.refreshAccessToken();

    }


    /**
     * Get All Accounts
     *
     * @param service
     * @return
     * @throws FMSException
     */
    @Retryable(
            value = FMSException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 1000))
    private void refreshAllAccounts(DataService service) throws FMSException {


        String accountsCSV = "('" + String.join("','", new String[]{
                AccountNameEnum.BUS_DRIVER_PATHA.getAccountName(),
                AccountNameEnum.BUS_DRIVER_SALARY_ALLOWANCE.getAccountName(),
                AccountNameEnum.BUS_CONDUCTOR_PATHA.getAccountName(),
                AccountNameEnum.BUS_CONDUCTOR_SALARY_ALLOWANCE.getAccountName(),
                AccountNameEnum.BUS_CHECKING_PATHA.getAccountName(),
                AccountNameEnum.COMMISSION_CHARGES.getAccountName(),
                AccountNameEnum.UNION_CHARGES.getAccountName(),
                AccountNameEnum.BUS_CLEANER_WAGES.getAccountName(),
                AccountNameEnum.OTHER_COSTS_OF_SALES_COS.getAccountName(),
                AccountNameEnum.GREASE_EXPENSE.getAccountName(),
                AccountNameEnum.UNCATEGORISED_EXPENSE.getAccountName(),
                AccountNameEnum.CASH_ON_HAND.getAccountName()

        }) + "')";


        log.debug("In clause is " + accountsCSV);


        accounts = service.executeQuery(String.format(ACCOUNT_QUERY_IN, accountsCSV))
                .getEntities()
                .stream()
                .map(entity -> (Account) entity)
                .collect(Collectors.toList());


    }


    /**
     * Get All Customers
     *
     * @param service
     * @return
     * @throws FMSException
     */
    @Retryable(
            value = FMSException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 1000))
    private void refreshAllCustomers(DataService service) throws FMSException {

        String customersCSV = "('" + String.join("','", new String[]{
                CustomerNameEnum.DHARMAPURI_HOSUR.getCustomerName(),
                CustomerNameEnum.DHARMAPURI_PAAVAKKAL.getCustomerName(),
                CustomerNameEnum.DHARMAPURI_SALEM.getCustomerName()}) + "')";


        customers = service.executeQuery(String.format(CUSTOMER_QUERY_IN, customersCSV))
                .getEntities()
                .stream()
                .map(entity -> (Customer) entity)
                .collect(Collectors.toList());

    }


    /**
     * Get All Products
     *
     * @param service
     * @return
     * @throws FMSException
     */
    @Retryable(
            value = FMSException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 1000))
    private void refreshAllProducts(DataService service) throws FMSException {


        String productCSV = "('" + String.join("','", new String[]{
                ProductNameEnum.DHARMAPURI_HOSUR.getProductName(),
                ProductNameEnum.DHARMAPURI_PAAVAKKAL.getProductName(),
                ProductNameEnum.DHARMAPURI_SALEM.getProductName(),
                ProductNameEnum.FUEL_DIESEL.getProductName()
        }) + "')";


        products = service.executeQuery(String.format(PRODUCT_QUERY_IN, productCSV))
                .getEntities()
                .stream()
                .map(entity -> (Item) entity)
                .collect(Collectors.toList());


    }


    /**
     * Get All Customers
     *
     * @param service
     * @return
     * @throws FMSException
     */
    @Retryable(
            value = FMSException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 1000))
    private void refreshAllPaymentMethods(DataService service) throws FMSException {


        String paymentMethodCSV = "('" + String.join("','", new String[]{"Cheque"}) + "')";


        paymentMethods = service.executeQuery(String.format(PAYMENT_METHOD_IN, paymentMethodCSV))
                .getEntities()
                .stream()
                .map(entity -> (PaymentMethod) entity)
                .collect(Collectors.toList());


    }


    private void refreshAllData(DataService service) throws FMSException {
        this.refreshAllAccounts(service);
        this.refreshAllCustomers(service);
        this.refreshAllProducts(service);
        this.refreshAllPaymentMethods(service);

    }


    @Retryable(
            value = FMSException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 1000))
    public SalesReceipt createSalesReceipt(Date receiptDate, CustomerNameEnum customerNameEnum, ProductNameEnum productNameEnum, BigDecimal amount) throws FMSException {

        DataService service = getDataService();
        refreshAllData(service);
        SalesReceipt salesReceipt = new SalesReceipt();
        salesReceipt.setTxnDate(receiptDate);
        Line line1 = getSalesItemLine(service, amount, productNameEnum,receiptDate);
        salesReceipt.setLine(Arrays.asList(line1));
        salesReceipt.setDepositToAccountRef(createRef(getPaymentAccount()));
        salesReceipt.setCustomerRef(createRef(getCustomer(customerNameEnum)));
        salesReceipt.setApplyTaxAfterDiscount(false);
        salesReceipt.setTotalAmt(amount);
        salesReceipt.setGlobalTaxCalculation(GlobalTaxCalculationEnum.NOT_APPLICABLE);
        return service.add(salesReceipt);

    }


    public Purchase createExpense(List<ExpenseDTO> expenseDTOs) throws FMSException {

        DataService service = getDataService();
        refreshAllData(service);
        Purchase purchase = new Purchase();
        purchase.setPaymentType(PaymentTypeEnum.CASH);
        purchase.setPaymentMethodRef(createRef(getPaymentMethod(PaymentMethodEnum.CHECK)));

        purchase.setTxnDate(expenseDTOs.size() > 0 ? expenseDTOs.get(0)
                .getExpenseDate() : Calendar.getInstance()
                .getTime());
        Account paymentAccount = getPaymentAccount();
        purchase.setAccountRef(createRef(paymentAccount));

        List<Line> lines = new ArrayList<>();

        expenseDTOs.forEach((dto) -> {
            Line line;

            try {
                if (dto.getAccountNameEnum() != null) {

                    line = getAccountBasedLine(service, dto.getExpenseAmount(), dto.getAccountNameEnum(), dto.getCustomerNameEnum(), dto.getDescription());

                } else {
                    if (dto.getProductNameEnum() != null) {
                        line = getItemBasedLine(service, dto.getExpenseAmount(), dto.getProductNameEnum(), dto.getCustomerNameEnum(), dto.getDescription());
                    } else {
                        throw new IllegalArgumentException("Either Account Name or Product Name is required to be able to create an expense.");
                    }
                }
            } catch (FMSException e) {
                throw new RuntimeException("Error getting Account based line!", e);
            }
            lines.add(line);
        });


        lines.forEach((line) -> log.info(" Amount is for :" + line.getDetailType() + ":::" + line.getAmount()));


        purchase.setLine(lines
                .stream()
                .filter(line -> line.getAmount() != null)
                .collect(Collectors.toList()));

        return service.add(purchase);


    }
}
