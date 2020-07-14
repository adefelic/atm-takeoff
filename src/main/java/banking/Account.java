package banking;

import java.util.List;

public class Account {
    private final String id;
    private final String pin;
    private double balance;
    private List<TransactionRecord> transactionHistory;

    public Account(String id, String pin, double balance) {
        this.id = id;
        this.pin = pin;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public List<TransactionRecord> getTransactionHistory() {
        return transactionHistory;
    }

    public void setBalance(double balance) {
        this.balance = balance;
        // todo update persistence with new balance
    }

    public void recordTransaction(TransactionRecord transactionRecord) {
        transactionHistory.add(0, transactionRecord);
    }
}
