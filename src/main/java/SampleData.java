import java.sql.SQLException;

public final class SampleData {
    private SampleData() { }

    public static void insertTenRecords(CustomerDAO customers, AccountDAO accounts) throws SQLException {
        if (customers.count() > 0) return;
        for (int i = 1; i <= 10; i++) {
            String suffix = String.format("%02d", i);
            Customer customer = new Customer("Sample", "Customer" + suffix, "Gaborone", "+267 700000" + suffix, "sample" + suffix, "Password123");
            customers.create(customer);
            Account account = switch (i % 3) {
                case 0 -> new SavingsAccount("S" + suffix, 1000 + i * 100);
                case 1 -> new InvestmentAccount("I" + suffix, 500 + i * 100);
                default -> new ChequeAccount("C" + suffix, 200 + i * 50, "Sample Employer", "Gaborone");
            };
            accounts.create(customer.getUsername(), account);
        }
    }
}
