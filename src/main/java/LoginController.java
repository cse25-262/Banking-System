public class LoginController {

    private final Bank bank = new Bank();
    private Customer loggedInCustomer;

    public boolean login(String username, String password) {
        loggedInCustomer = bank.login(username, password);
        return loggedInCustomer != null;
    }

    public boolean register(String firstName, String surname, String address, String phone, String username, String password) {
        try {
            Customer customer = new Customer(firstName, surname, address, phone, username, password);
            bank.registerCustomer(customer);
            loggedInCustomer = customer;
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public AccountController createAccountController() {
        if (loggedInCustomer == null) {
            throw new IllegalStateException("Log in before opening the account screen.");
        }
        return new AccountController(loggedInCustomer, bank);
    }

    public void startMonthlyInterestSchedule() {
        bank.startMonthlyInterestSchedule();
    }

    public void stopMonthlyInterestSchedule() {
        bank.stopMonthlyInterestSchedule();
    }
}
