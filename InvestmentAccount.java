
public class InvestmentAccount extends Account {

    public InvestmentAccount(String accountNumber, double balance) {
        super(accountNumber, balance);

        if (balance < 500) {
            throw new IllegalArgumentException(
                "Minimum investment is BWP 500"
            );
        }
    }

    public void addInterest() {
        double interest = balance * 0.05;
        balance = balance + interest;

        System.out.println("Investment interest added!");
    }
}
