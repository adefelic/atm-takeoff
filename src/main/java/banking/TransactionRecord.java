package banking;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionRecord that = (TransactionRecord) o;
        return timestamp == that.timestamp &&
                Double.compare(that.amount, amount) == 0 &&
                Double.compare(that.balance, balance) == 0 &&
                Double.compare(that.overdraftFee, overdraftFee) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, amount, balance, overdraftFee);
    }
}
