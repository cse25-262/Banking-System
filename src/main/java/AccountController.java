

public class AccountController {

    Account account;

    public AccountController() {
        account = new InvestmentAccount("1002", 1000);
    }

    public String deposit(String amountText) {
        try {
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                return "Enter a positive amount.";
            }

            account.deposit(amount);
            return "Deposit successful!";

        } catch (NumberFormatException e) {
            return "Please enter a valid number.";
        }
    }

    public String withdraw(String amountText) {
        try {
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                return "Enter a positive amount.";
            }

            if (amount > account.balance) {
                return "Insufficient funds.";
            }

            account.withdraw(amount);
            return "Withdrawal successful!";

        } catch (NumberFormatException e) {
            return "Please enter a valid number.";
        }
    }

    public double getBalance() {
        return account.balance;
    }

    public String getAccountNumber() {
        return account.getAccountNumber();
    }

    public String getHistory() {
        String history = "Transaction History:\n";

        for (Transaction transaction : account.transactions) {
            history = history + transaction.type
                    + ": BWP " + transaction.amount + "\n";
        }

        return history;
    }
}
