import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.YearMonth;

public abstract class Account {
    public static final String DEFAULT_BRANCH = "Gaborone";

    private final String accountNumber;
    private final String branch;
    private double balance;
    private final List<Transaction> transactions = new ArrayList<>();
    private YearMonth lastInterestPeriod;

    protected Account(String accountNumber, double balance) {
        this(accountNumber, balance, DEFAULT_BRANCH);
    }

    protected Account(String accountNumber, double balance, String branch) {
        if (accountNumber == null || accountNumber.isBlank()) throw new IllegalArgumentException("Account number is required.");
        if (!Double.isFinite(balance) || balance < 0) throw new IllegalArgumentException("Opening balance must be non-negative.");
        if (branch == null || branch.isBlank()) throw new IllegalArgumentException("Branch is required.");
        this.accountNumber = accountNumber;
        this.branch = branch.trim();
        this.balance = balance;
    }

    public synchronized void deposit(double amount) { deposit(amount, "Deposit"); }

    public synchronized void deposit(double amount, String description) {
        requirePositive(amount, "Deposit");
        addToBalance(amount, "Deposit");
        transactions.add(new Transaction(TransactionType.DEPOSIT, amount, description));
    }

    public synchronized void withdraw(double amount) {
        requirePositive(amount, "Withdrawal");
        if (amount > balance) throw new IllegalStateException("Insufficient funds.");
        balance -= amount;
        transactions.add(new Transaction(TransactionType.WITHDRAWAL, amount, "Withdrawal"));
    }

    protected final synchronized void credit(double amount, TransactionType type) {
        requirePositive(amount, "Credit");
        addToBalance(amount, "Credit");
        transactions.add(new Transaction(type, amount, type.name()));
    }

    final synchronized void restoreTransaction(Transaction transaction) { transactions.add(transaction); }
    final synchronized boolean interestWasAppliedIn(YearMonth period) {
        return period.equals(lastInterestPeriod);
    }
    final synchronized void markInterestAppliedIn(YearMonth period) {
        lastInterestPeriod = period;
    }
    final synchronized void restoreInterestPeriod(String period) {
        if (period != null && !period.isBlank()) lastInterestPeriod = YearMonth.parse(period);
    }
    final synchronized String getLastInterestPeriod() { return lastInterestPeriod == null ? null : lastInterestPeriod.toString(); }

    private static void requirePositive(double amount, String operation) {
        if (!Double.isFinite(amount) || amount <= 0) throw new IllegalArgumentException(operation + " amount must be positive.");
    }

    private void addToBalance(double amount, String operation) {
        double newBalance = balance + amount;
        if (!Double.isFinite(newBalance)) {
            throw new IllegalArgumentException(operation + " would exceed the supported balance.");
        }
        balance = newBalance;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getBranch() { return branch; }
    public synchronized double getBalance() { return balance; }
    public synchronized List<Transaction> getTransactions() { return List.copyOf(transactions); }

    @Override public String toString() { return getClass().getSimpleName() + " - " + accountNumber; }
}
