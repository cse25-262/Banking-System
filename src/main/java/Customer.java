

import java.util.ArrayList;

public class Customer {

    String name;
    String address;
    String phoneNumber;

    ArrayList<Account> accounts = new ArrayList<>();

    public Customer(String name, String address, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void showDetails() {
        System.out.println("Name: " + name);
        System.out.println("Address: " + address);
        System.out.println("Phone: " + phoneNumber);
    }

    public void showAccounts() {
        for (Account account : accounts) {
            System.out.println("Account: " + account.getAccountNumber());
            account.showBalance();
        }
    }
}
