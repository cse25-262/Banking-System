
public class LoginController {

    Bank bank = new Bank();

    public boolean login(String username, String password) {
        return bank.login(username, password);
    }
}
