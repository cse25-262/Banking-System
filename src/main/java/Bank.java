
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.YearMonth;

public class Bank {
    private static final Logger LOGGER = Logger.getLogger(Bank.class.getName());
    private final List<Customer> customers = new ArrayList<>();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private ScheduledExecutorService interestScheduler;

    public Bank() {
        try {
            SampleData.insertTenRecords(customerDAO, accountDAO);
            for (Customer customer : customerDAO.readAll()) {
                for (Account account : accountDAO.readByCustomer(customer.getUsername())) customer.addAccount(account);
                customers.add(customer);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load banking data.", e);
        }
    }

    public void registerCustomer(Customer customer) {
        if (customer == null) throw new IllegalArgumentException("Customer is required.");
        if (customers.stream().anyMatch(existing -> existing.getUsername().equalsIgnoreCase(customer.getUsername()))) throw new IllegalArgumentException("Username is already registered.");
        try { customerDAO.create(customer); customers.add(customer); }
        catch (SQLException e) { throw new IllegalStateException("Could not save customer.", e); }
    }

    public Customer login(String username, String password) {
        if (username == null || password == null) return null;
        return customers.stream().filter(c -> c.getUsername().equalsIgnoreCase(username) && c.checkPassword(password)).findFirst().orElse(null);
    }

    public String generateAccountNumber() { return "A" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(); }
    public List<Customer> getCustomers() { return Collections.unmodifiableList(customers); }

    public Account openAccount(Customer customer, AccountType type, double openingBalance, String employerName, String employerAddress) {
        return openAccount(customer, type, openingBalance, Account.DEFAULT_BRANCH, employerName, employerAddress);
    }

    public Account openAccount(Customer customer, AccountType type, double openingBalance, String branch, String employerName, String employerAddress) {
        if (customer == null || !customers.contains(customer)) throw new IllegalArgumentException("Customer must be registered before opening an account.");
        if (type == null) throw new IllegalArgumentException("Select an account type.");
        String number = generateAccountNumber();
        Account account = switch (type) {
            case SAVINGS -> new SavingsAccount(number, openingBalance, branch);
            case INVESTMENT -> new InvestmentAccount(number, openingBalance, branch);
            case CHEQUE -> new ChequeAccount(number, openingBalance, branch, employerName, employerAddress);
        };
        try { accountDAO.create(customer.getUsername(), account); customer.addAccount(account); return account; }
        catch (SQLException e) { throw new IllegalStateException("Could not save account.", e); }
    }

    public void saveAccount(Account account) {
        try { accountDAO.update(account); }
        catch (SQLException e) { throw new IllegalStateException("Could not save account transaction.", e); }
    }

    public AccountDAO getAccountDAO() { return accountDAO; }

    public void applyMonthlyInterest() {
        for (Customer customer : customers) {
            for (Account account : customer.getAccounts()) {
                applyInterest(account);
            }
        }
    }

    public boolean applyInterest(Account account) {
        if (!(account instanceof InterestBearing interestBearing)) return false;
        if (!account.markInterestAppliedIn(YearMonth.now())) return false;
        interestBearing.applyInterest();
        saveAccount(account);
        return true;
    }

    public synchronized void startMonthlyInterestSchedule() {
        if (interestScheduler != null) return;
        interestScheduler = Executors.newSingleThreadScheduledExecutor(task -> {
            Thread thread = new Thread(task, "bank-monthly-interest");
            thread.setDaemon(true);
            return thread;
        });
        long monthInDays = 30;
        interestScheduler.scheduleAtFixedRate(() -> {
            try { applyMonthlyInterest(); }
            catch (RuntimeException e) { LOGGER.log(Level.SEVERE, "Monthly interest application failed.", e); }
        }, monthInDays, monthInDays, TimeUnit.DAYS);
    }

    public synchronized void stopMonthlyInterestSchedule() {
        if (interestScheduler != null) {
            interestScheduler.shutdownNow();
            interestScheduler = null;
        }
    }
}
