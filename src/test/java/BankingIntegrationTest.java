import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BankingIntegrationTest {
    @Test
    void customerCanLogInDepositAndReadUpdatedBalanceFromDatabase() throws Exception {
        String previousUrl = System.getProperty("bank.db.url");
        System.setProperty("bank.db.url", "jdbc:h2:mem:banking-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        try {
            Bank bank = new Bank();
            Customer customer = new Customer("Test", "Customer", "Gaborone", "12345678", "integration-user", "TestPassword1");
            bank.registerCustomer(customer);

            Customer loggedIn = bank.login("integration-user", "TestPassword1");
            assertNotNull(loggedIn);
            Account account = bank.openAccount(loggedIn, AccountType.SAVINGS, 100, "", "");
            AccountController controller = new AccountController(loggedIn, bank);
            controller.selectAccount(account.getAccountNumber());
            assertTrue(controller.deposit("25.50").startsWith("Deposit successful"));

            Account persisted = bank.getAccountDAO().read(account.getAccountNumber());
            assertNotNull(persisted);
            assertEquals(Account.DEFAULT_BRANCH, persisted.getBranch());
            assertEquals(125.50, persisted.getBalance(), 0.0001);
            assertEquals(1, persisted.getTransactions().size());
        } finally {
            if (previousUrl == null) System.clearProperty("bank.db.url");
            else System.setProperty("bank.db.url", previousUrl);
        }
    }

    @Test
    void firstLaunchSeedsTenCustomersWithOneAccountEach() {
        String previousUrl = System.getProperty("bank.db.url");
        System.setProperty("bank.db.url", "jdbc:h2:mem:sample-data-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        try {
            Bank bank = new Bank();

            assertEquals(10, bank.getCustomers().size());
            assertTrue(bank.getCustomers().stream().allMatch(customer -> customer.getAccounts().size() == 1));
            assertNotNull(bank.login("sample01", "Password123"));
        } finally {
            if (previousUrl == null) System.clearProperty("bank.db.url");
            else System.setProperty("bank.db.url", previousUrl);
        }
    }

    @Test
    void existingDatabaseReceivesDefaultBranchColumn() throws Exception {
        String previousUrl = System.getProperty("bank.db.url");
        String url = "jdbc:h2:mem:migration-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1";
        System.setProperty("bank.db.url", url);
        try {
            try (Connection connection = DriverManager.getConnection(url, "sa", ""); Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE accounts (account_number VARCHAR(30) PRIMARY KEY, customer_username VARCHAR(80) NOT NULL, account_type VARCHAR(20) NOT NULL, balance DOUBLE PRECISION NOT NULL, employer_name VARCHAR(150), employer_address VARCHAR(250), last_interest_period CHAR(7))");
                statement.execute("INSERT INTO accounts(account_number, customer_username, account_type, balance) VALUES('LEGACY-1', 'legacy-user', 'SAVINGS', 100)");
            }

            DatabaseManager.initialize();
            try (Connection connection = DatabaseManager.getConnection(); Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery("SELECT branch FROM accounts WHERE account_number='LEGACY-1'")) {
                assertTrue(result.next());
                assertEquals(Account.DEFAULT_BRANCH, result.getString("branch"));
            }
        } finally {
            if (previousUrl == null) System.clearProperty("bank.db.url");
            else System.setProperty("bank.db.url", previousUrl);
        }
    }
}
