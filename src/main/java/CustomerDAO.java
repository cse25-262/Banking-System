import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public CustomerDAO() { DatabaseManager.initialize(); }

    public void create(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers(username,first_name,surname,address,phone_number,password_hash) VALUES(?,?,?,?,?,?)";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, customer); p.executeUpdate();
        }
    }

    public Customer read(String username) throws SQLException {
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM customers WHERE username=?")) {
            p.setString(1, username);
            try (ResultSet r = p.executeQuery()) { return r.next() ? map(r) : null; }
        }
    }

    public List<Customer> readAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT * FROM customers ORDER BY username")) {
            while (r.next()) list.add(map(r));
        }
        return list;
    }

    public void update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET first_name=?,surname=?,address=?,phone_number=?,password_hash=? WHERE username=?";
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, customer.getFirstName()); p.setString(2, customer.getSurname()); p.setString(3, customer.getAddress());
            p.setString(4, customer.getPhoneNumber()); p.setString(5, customer.passwordForPersistence()); p.setString(6, customer.getUsername());
            p.executeUpdate();
        }
    }

    public void delete(String username) throws SQLException {
        try (Connection c = DatabaseManager.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM customers WHERE username=?")) {
            p.setString(1, username); p.executeUpdate();
        }
    }

    public int count() throws SQLException {
        try (Connection c = DatabaseManager.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT COUNT(*) FROM customers")) { r.next(); return r.getInt(1); }
    }

    private static void bind(PreparedStatement p, Customer c) throws SQLException {
        p.setString(1,c.getUsername()); p.setString(2,c.getFirstName()); p.setString(3,c.getSurname());
        p.setString(4,c.getAddress()); p.setString(5,c.getPhoneNumber()); p.setString(6,c.passwordForPersistence());
    }
    private static Customer map(ResultSet r) throws SQLException {
        return Customer.fromStored(r.getString("first_name"), r.getString("surname"), r.getString("address"), r.getString("phone_number"), r.getString("username"), r.getString("password_hash"));
    }
}
