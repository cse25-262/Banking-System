
public class ChequeAccount extends Account {

    String employerName;

    public ChequeAccount(String accountNumber,
                         double balance,
                         String employerName) {

        super(accountNumber, balance);
        this.employerName = employerName;
    }

    public void showEmployer() {
        System.out.println("Employer: " + employerName);
    }
}
