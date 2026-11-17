
public class SavingsAccount extends Account {

    public SavingsAccount(String accountNumber, double balance) {
        super(accountNumber, balance);
    }

    public void addInterest() {
        double interest = balance * 0.0005;
        balance = balance + interest;
        System.out.println("Interest added successfully!");
    }
}
