

public class Main {
    public static void main(String[] args) {

        Bank bank = new Bank();

        if (bank.login("Lyne", "1234")) {
            System.out.println("Login successful!");
            System.out.println("Welcome to Banking System");
        } else {
            System.out.println("Incorrect username or password");
        }
    }
}
