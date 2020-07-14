package banking;

import java.util.HashMap;
import java.util.Map;

import static banking.TransactionRecord.NO_TRANSACTION;

/**
 * the AtmService class is designed to simulate a remote banking service
 */
public class AtmService {

    private final static double OVERDRAFT_FEE = 5.;
    private Map<String, Account> accounts;

    public AtmService() {
        // todo load from persistence instead of hardcoding account values
        accounts = new HashMap<>();
        accounts.put("2859459814", new Account("2859459814", "7386", 10.24));
        accounts.put("1434597300", new Account("2859459814", "4557", 90000.55));
        accounts.put("7089382418", new Account("2859459814", "0075", .0));
        accounts.put("2001377812", new Account("2859459814", "5950", 60.));
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
        if (accounts.containsKey(accountId)) {
            Account account = accounts.get(accountId);
            double balance = account.getBalance();
            if (balance < 0) {
                // current application logic should prevent this case.
                return NO_TRANSACTION;
            }
            boolean isOverdraw = balance - amount < 0.;
            account.setBalance(balance - amount - (isOverdraw ? OVERDRAFT_FEE : 0.));
            TransactionRecord transactionRecord = new TransactionRecord(System.currentTimeMillis(), amount, account.getBalance(), (isOverdraw ? OVERDRAFT_FEE : 0.));
            account.recordTransaction(transactionRecord);
            return transactionRecord;
        }
        return NO_TRANSACTION;
    }

    public void deposit(String accountId, int amount) {}

    public double balance(String accountId) {
        if (accounts.containsKey(accountId)) {
            return accounts.get(accountId).getBalance();
        }
        return -1.;
    }

    public void history(String accountId) {}
}
