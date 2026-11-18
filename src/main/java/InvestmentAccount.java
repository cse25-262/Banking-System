public class InvestmentAccount extends Account implements InterestBearing {
    public InvestmentAccount(String accountNumber, double balance) {
        this(accountNumber, balance, DEFAULT_BRANCH);
    }

    public InvestmentAccount(String accountNumber, double balance, String branch) {
        super(accountNumber, balance, branch);
        if (balance < 500) throw new IllegalArgumentException("Minimum investment is BWP 500.");
    }

    @Override
    public double calculateInterest() { return getBalance() * 0.05; }

    @Override
    public void applyInterest() {
        double interest = calculateInterest();
        if (interest > 0) credit(interest, TransactionType.INTEREST);
    }
}
