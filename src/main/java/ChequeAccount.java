public class ChequeAccount extends Account {
    private final String employerName;
    private final String employerAddress;

    public ChequeAccount(String accountNumber, double balance, String employerName, String employerAddress) {
        this(accountNumber, balance, DEFAULT_BRANCH, employerName, employerAddress);
    }

    public ChequeAccount(String accountNumber, double balance, String branch, String employerName, String employerAddress) {
        super(accountNumber, balance, branch);
        if (employerName == null || employerName.isBlank()) throw new IllegalArgumentException("Employer name is required.");
        if (employerAddress == null || employerAddress.isBlank()) throw new IllegalArgumentException("Employer address is required.");
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }
}
