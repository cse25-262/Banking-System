
public class Bank {

    String username = "Lyne";
    String password = "1234";

    public boolean login(String user, String pass) {

        if (user.equals(username) && pass.equals(password)) {
            return true;
        } else {
            return false;
        }
    }
}
