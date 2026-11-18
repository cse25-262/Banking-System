public class SavingsAccount extends Account implements InterestBearing {
    public SavingsAccount(String accountNumber, double balance) { this(accountNumber, balance, DEFAULT_BRANCH); }
    public SavingsAccount(String accountNumber, double balance, String branch) { super(accountNumber, balance, branch); }

    @Override
    public void withdraw(double amount) {
        throw new UnsupportedOperationException("Savings accounts do not allow withdrawals.");
    }

    @Override
    public double calculateInterest() { return getBalance() * 0.0005; }

    @Override
    public void applyInterest() {
        double interest = calculateInterest();
        if (interest > 0) credit(interest, TransactionType.INTEREST);
    }
}
