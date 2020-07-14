package banking;

public class TransactionRecord {

    public final static TransactionRecord NO_TRANSACTION = new TransactionRecord(0, 0, 0, 0);
    public final long timestamp;
    public final double amount;
    public final double balance;
    public final double overdraftFee;

    public TransactionRecord(long timestamp, double amount, double balance, double overdraftFee) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.balance = balance;
        this.overdraftFee = overdraftFee;
    }
}
