package banking;

import app.ITimeSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static banking.TransactionRecord.NO_TRANSACTION;

/**
 * the AtmService class is designed to simulate a remote banking service
 */
public class AtmService {

    private final static double OVERDRAFT_FEE = 5.;
    private final ITimeSource timeSource;
    private Map<String, Account> accounts;

    public AtmService(ITimeSource timeSource) {
        this.timeSource = timeSource;
        // todo load from persistence instead of hardcoding account values
        accounts = new HashMap<>();
        accounts.put("2859459814", new Account("2859459814", "7386", 10.24));
        accounts.put("1434597300", new Account("1434597300", "4557", 90000.55));
        accounts.put("7089382418", new Account("7089382418", "0075", .0));
        accounts.put("2001377812", new Account("2001377812", "5950", 60.));
    }

    public boolean authorize(String accountId, String pin) {
        // todo return an access token instead and make each service "endpoint" require a valid token
        return accounts.containsKey(accountId) && accounts.get(accountId).getPin().equals(pin);
    }

    /**
     * upon successful withdrawal, returns a record of an attempted withdrawal transaction.
     * charges overcharge fee
     * @param accountId
     * @param amount
     * @return
     */
    public TransactionRecord withdraw(String accountId, int amount) {
        // todo require auth token
        if (amount <= 0 || !accounts.containsKey(accountId)) {
            return NO_TRANSACTION;
        }
        Account account = accounts.get(accountId);
        double balance = account.getBalance();
        if (balance <= 0) {
            // current application logic should prevent this case.
            return NO_TRANSACTION;
        }
        boolean isOverdraw = balance - amount < 0.;
        account.setBalance(balance - amount - (isOverdraw ? OVERDRAFT_FEE : 0.));
        TransactionRecord transactionRecord = new TransactionRecord(timeSource.currentTimeMillis(), amount * -1, account.getBalance(), (isOverdraw ? OVERDRAFT_FEE : 0.));
        account.recordTransaction(transactionRecord);
        return transactionRecord;
    }

    public TransactionRecord deposit(String accountId, int amount) {
        // todo require auth token
        if (amount <= 0 || !accounts.containsKey(accountId)) {
            return NO_TRANSACTION;
        }
        Account account = accounts.get(accountId);
        double balance = account.getBalance();
        account.setBalance(balance + amount);

        TransactionRecord transactionRecord = new TransactionRecord(timeSource.currentTimeMillis(), amount, account.getBalance(), 0);
        account.recordTransaction(transactionRecord);
        return transactionRecord;
    }

    public double balance(String accountId) {
        // todo require auth token
        if (accounts.containsKey(accountId)) {
            return accounts.get(accountId).getBalance();
        }
        return -1.;
    }

    public List<TransactionRecord> history(String accountId) {
        // todo require auth token
        if (accounts.containsKey(accountId)) {
            return accounts.get(accountId).getTransactionHistory();
        }
        return null;
    }
}
