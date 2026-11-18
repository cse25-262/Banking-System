public class Transaction {
    private final TransactionType type;
    private final double amount;
    private final String description;

    public Transaction(TransactionType type, double amount) { this(type, amount, type == null ? null : type.name()); }

    public Transaction(TransactionType type, double amount, String description) {
        if (type == null) throw new IllegalArgumentException("Transaction type is required.");
        if (!Double.isFinite(amount) || amount <= 0) throw new IllegalArgumentException("Transaction amount must be positive.");
        this.type = type;
        this.amount = amount;
        this.description = description == null || description.isBlank() ? type.name() : description;
    }

    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }

    @Override
    public String toString() { return type + ": BWP " + amount + " (" + description + ")"; }
}
