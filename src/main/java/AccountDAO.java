import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    public AccountDAO() { DatabaseManager.initialize(); }

    public void create(String username, Account account) throws SQLException {
        String sql = "INSERT INTO accounts(account_number,customer_username,account_type,balance,branch,employer_name,employer_address) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, account.getAccountNumber()); p.setString(2, username); p.setString(3, typeOf(account).name()); p.setDouble(4, account.getBalance());
            p.setString(5, account.getBranch());
            if (account instanceof ChequeAccount cheque) { p.setString(6, cheque.getEmployerName()); p.setString(7, cheque.getEmployerAddress()); }
            else { p.setNull(6, Types.VARCHAR); p.setNull(7, Types.VARCHAR); }
            p.executeUpdate();
        }
    }

    public Account read(String accountNumber) throws SQLException {
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM accounts WHERE account_number=?")) {
            p.setString(1, accountNumber);
            try (ResultSet r = p.executeQuery()) { return r.next() ? map(c, r) : null; }
        }
    }

    public List<Account> readByCustomer(String username) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM accounts WHERE customer_username=? ORDER BY account_number")) {
            p.setString(1, username);
            try (ResultSet r = p.executeQuery()) { while (r.next()) accounts.add(map(c, r)); }
        }
        return accounts;
    }

    public void update(Account account) throws SQLException {
        try (Connection c = DatabaseManager.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement p = c.prepareStatement("UPDATE accounts SET balance=?,branch=?,last_interest_period=? WHERE account_number=?")) {
                p.setDouble(1, account.getBalance()); p.setString(2, account.getBranch()); p.setString(3, account.getLastInterestPeriod()); p.setString(4, account.getAccountNumber()); p.executeUpdate();
            }
            try (PreparedStatement p = c.prepareStatement("DELETE FROM transactions WHERE account_number=?")) {
                p.setString(1, account.getAccountNumber()); p.executeUpdate();
            }
            try (PreparedStatement p = c.prepareStatement("INSERT INTO transactions(account_number,transaction_type,amount,description) VALUES(?,?,?,?)")) {
                for (Transaction t : account.getTransactions()) {
                    p.setString(1, account.getAccountNumber()); p.setString(2, t.getType().name()); p.setDouble(3, t.getAmount()); p.setString(4, t.getDescription()); p.addBatch();
                }
                p.executeBatch();
            }
            c.commit();
        } catch (SQLException e) { throw e; }
    }

    public void delete(String accountNumber) throws SQLException {
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM accounts WHERE account_number=?")) {
            p.setString(1, accountNumber); p.executeUpdate();
        }
    }

    private static Account map(Connection c, ResultSet r) throws SQLException {
        String number = r.getString("account_number");
        double balance = r.getDouble("balance");
        String branch = r.getString("branch");
        if (branch == null || branch.isBlank()) branch = Account.DEFAULT_BRANCH;
        AccountType type = AccountType.valueOf(r.getString("account_type"));
        Account account = switch (type) {
            case SAVINGS -> new SavingsAccount(number, balance, branch);
            case INVESTMENT -> new InvestmentAccount(number, balance, branch);
            case CHEQUE -> new ChequeAccount(number, balance, branch, r.getString("employer_name"), r.getString("employer_address"));
        };
        account.restoreInterestPeriod(r.getString("last_interest_period"));
        try (PreparedStatement p = c.prepareStatement("SELECT transaction_type,amount,description FROM transactions WHERE account_number=? ORDER BY transaction_id")) {
            p.setString(1, number);
            try (ResultSet transactions = p.executeQuery()) {
                while (transactions.next()) account.restoreTransaction(new Transaction(TransactionType.valueOf(transactions.getString(1)), transactions.getDouble(2), transactions.getString(3)));
            }
        }
        return account;
    }

    private static AccountType typeOf(Account account) {
        if (account instanceof SavingsAccount) return AccountType.SAVINGS;
        if (account instanceof InvestmentAccount) return AccountType.INVESTMENT;
        if (account instanceof ChequeAccount) return AccountType.CHEQUE;
        throw new IllegalArgumentException("Unsupported account type.");
    }
}
