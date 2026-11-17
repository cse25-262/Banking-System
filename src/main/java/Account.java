
import java.util.ArrayList;

public class Account {

    private String accountNumber;
protected double balance;
    ArrayList<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance = balance + amount;
            transactions.add(new Transaction("Deposit", amount));
            System.out.println("Deposit successful!");
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount.");
        } else if (amount > balance) {
            System.out.println("Insufficient funds.");
        } else {
            balance = balance - amount;
            transactions.add(new Transaction("Withdrawal", amount));
            System.out.println("Withdrawal successful!");
        }
    }

    public void showBalance() {
        System.out.println("Balance: BWP " + balance);
    }

    public void showTransactionHistory() {
        System.out.println("Transaction History:");

        for (Transaction transaction : transactions) {
            transaction.showTransaction();
        }
    }

public String getAccountNumber() {
    return accountNumber;
}

}

