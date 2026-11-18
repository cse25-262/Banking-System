

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Customer {
    private final String firstName, surname, address, phoneNumber, username;
    private final byte[] passwordSalt;
    private final byte[] passwordHash;
    private final List<Account> accounts = new ArrayList<>();

    public Customer(String firstName, String surname, String address, String phoneNumber, String username, String password) {
        if (firstName == null || firstName.isBlank() || surname == null || surname.isBlank()) throw new IllegalArgumentException("First name and surname are required.");
        if (username == null || username.isBlank() || password == null || password.isBlank()) throw new IllegalArgumentException("Username and password are required.");
        this.firstName = firstName;
        this.surname = surname;
        this.address = address == null ? "" : address;
        this.phoneNumber = phoneNumber == null ? "" : phoneNumber;
        this.username = username;
        this.passwordSalt = new byte[16];
        new SecureRandom().nextBytes(passwordSalt);
        this.passwordHash = hash(password, passwordSalt);
    }

    private Customer(String firstName, String surname, String address, String phoneNumber, String username, byte[] salt, byte[] hash) {
        this.firstName = firstName; this.surname = surname; this.address = address == null ? "" : address;
        this.phoneNumber = phoneNumber == null ? "" : phoneNumber; this.username = username;
        this.passwordSalt = salt; this.passwordHash = hash;
    }

    public String getFirstName() { return firstName; }
    public String getSurname() { return surname; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getUsername() { return username; }
    public boolean checkPassword(String attempt) { return attempt != null && MessageDigest.isEqual(passwordHash, hash(attempt, passwordSalt)); }
    String passwordForPersistence() { return Base64.getEncoder().encodeToString(passwordSalt) + "." + Base64.getEncoder().encodeToString(passwordHash); }
    static Customer fromStored(String firstName, String surname, String address, String phoneNumber, String username, String storedPassword) {
        String[] parts = storedPassword.split("\\.", 2);
        if (parts.length != 2) throw new IllegalArgumentException("Stored password data is invalid.");
        return new Customer(firstName, surname, address, phoneNumber, username, Base64.getDecoder().decode(parts[0]), Base64.getDecoder().decode(parts[1]));
    }
    private static byte[] hash(String password, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65_536, 256);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) { throw new IllegalStateException("Password hashing is unavailable.", e); }
    }
    public List<Account> getAccounts() { return Collections.unmodifiableList(accounts); }
    public void addAccount(Account account) {
        if (account == null) throw new IllegalArgumentException("Account is required.");
        accounts.add(account);
    }
}
