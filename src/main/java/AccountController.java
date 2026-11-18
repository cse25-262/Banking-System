import java.util.List;
import java.util.Locale;

public class AccountController {
    private Account account;
    private final Customer customer;
    private final Bank bank;

    public AccountController(Customer customer, Bank bank) {
        if (customer == null || bank == null) throw new IllegalArgumentException("Customer and bank are required.");
        this.customer = customer;
        this.bank = bank;
    }

    public void selectAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            account = null;
            return;
        }
        account = customer.getAccounts().stream()
            .filter(candidate -> candidate.getAccountNumber().equals(accountNumber))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Select an account belonging to this customer."));
    }

    public String getCustomerDisplayName() {
        return customer.getFirstName() + " " + customer.getSurname();
    }

    public String getDefaultBranch() { return Account.DEFAULT_BRANCH; }

    public List<String> getAccountNumbers() {
        return customer.getAccounts().stream().map(Account::getAccountNumber).toList();
    }

    public List<String> getSupportedAccountTypes() {
        return List.of("Savings", "Investment", "Cheque");
    }

    public String deposit(String amountText) {
        try {
            requireAccount();
            account.deposit(Double.parseDouble(amountText));
            bank.saveAccount(account);
            return "Deposit successful.";
        } catch (NumberFormatException e) { return "Please enter a valid number."; }
        catch (IllegalArgumentException | IllegalStateException e) { return e.getMessage(); }
    }

    public String withdraw(String amountText) {
        try {
            requireAccount();
            account.withdraw(Double.parseDouble(amountText));
            bank.saveAccount(account);
            return "Withdrawal successful.";
        } catch (NumberFormatException e) { return "Please enter a valid number."; }
        catch (IllegalArgumentException | IllegalStateException | UnsupportedOperationException e) { return e.getMessage(); }
    }

    public String applyInterest() {
        requireAccount();
        if (!(account instanceof InterestBearing)) return "This account does not earn interest.";
        if (!bank.applyInterest(account)) return "Interest was already applied this month.";
        return "Interest applied. New balance: BWP " + account.getBalance();
    }

    public double getBalance() { requireAccount(); return account.getBalance(); }

    public String getHistory() {
        requireAccount();
        StringBuilder history = new StringBuilder("Transaction History:\n");
        for (Transaction transaction : account.getTransactions()) history.append(transaction).append('\n');
        return history.toString();
    }

    public String openAccount(String accountType, String openingBalance, String branch, String employerName, String employerAddress) {
        double amount = Double.parseDouble(openingBalance);
        AccountType type = switch (accountType == null ? "" : accountType.toLowerCase(Locale.ROOT)) {
            case "savings" -> AccountType.SAVINGS;
            case "investment" -> AccountType.INVESTMENT;
            case "cheque" -> AccountType.CHEQUE;
            default -> throw new IllegalArgumentException("Select a valid account type.");
        };
        account = bank.openAccount(customer, type, amount, branch, employerName, employerAddress);
        return account.getAccountNumber();
    }

    private void requireAccount() {
        if (account == null) throw new IllegalStateException("Select or open an account first.");
    }
}
