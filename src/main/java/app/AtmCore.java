package app;

import banking.AtmService;
import banking.TransactionRecord;
import machine.AtmMachine;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * AtmCore holds business logic that uses both remote banking services (AtmService) and local machine information (AtmMachine)
 */
public class AtmCore {

    private static final long LOGIN_TIMEOUT_MS = 1000 * 60 * 2;
    private ITimeSource timeSource;
    private final AtmMachine atmMachine;
    private final AtmService atmService;
    private String currentLoggedInAccountId; // this is set to null if there isn't an active login
    private long lastInteractionTimestamp;

    public AtmCore(ITimeSource timeSource, AtmService atmService, AtmMachine atmMachine) {
        this.timeSource = timeSource;
        this.atmService = atmService;
        this.atmMachine = atmMachine;
    }

    public String login(String accountId, String pin) {
        if (atmService.authorize(accountId, pin)) {
            currentLoggedInAccountId = accountId;
            return currentLoggedInAccountId + " successfully authorized.";
        }
        return "Authorization failed.";
    }

    public String logout() {
        if (currentLoggedInAccountId != null) {
            String message = "Account " + currentLoggedInAccountId + " logged out.";
            currentLoggedInAccountId = null;
            return message;
        } else {
            return "No account is currently authorized.";
        }
    }

    public boolean hasLogin() {
        return currentLoggedInAccountId != null && lastInteractionTimestamp + LOGIN_TIMEOUT_MS > timeSource.currentTimeMillis();
    }

    public String withdraw(int requestedAmount) {
        if (requestedAmount <= 0) {
            return "Can only withdraw positive amounts of money.";
        }

        if (requestedAmount % 20 != 0) {
            return "Only multiples of 20 may be withdrawn.";
        }

        if (atmService.balance(currentLoggedInAccountId) <= 0.) {
            return "Your account is overdrawn! You may not make withdrawals at this time.";
        }

        int dispensibleAmount = atmMachine.getDispensibleAmountOfTargetValue(requestedAmount);

        if (dispensibleAmount <= 0.) {
            return "Unable to process your withdrawal at this time.";
        }

        StringBuilder stringBuilder = new StringBuilder();
        TransactionRecord transactionRecord = atmService.withdraw(currentLoggedInAccountId, dispensibleAmount);
        if (!transactionRecord.equals(TransactionRecord.NO_TRANSACTION)) {
            atmMachine.dispenseMoney(dispensibleAmount);
            if (dispensibleAmount < requestedAmount) {
                stringBuilder.append("Unable to dispense full amount requested at this time.\n");
            }
            stringBuilder.append("Amount dispensed: ");
            stringBuilder.append(transactionRecord.amount * -1);
            stringBuilder.append("\n");
            if (transactionRecord.overdraftFee > 0) {
                stringBuilder.append("You have been charged an overdraft fee of $");
                stringBuilder.append(transactionRecord.overdraftFee);
                stringBuilder.append(". ");
            }
            stringBuilder.append("Current balance: ");
            stringBuilder.append(transactionRecord.balance);

            return stringBuilder.toString();
        }
        return "No transaction (something went wrong in the AtmService)";
    }

    public String deposit(int amountToDeposit) {
        // deposits can only fail if input is bad
        if (amountToDeposit <= 0) {
            return "Can only deposit positive amounts of money.";
        }

        TransactionRecord transactionRecord = atmService.deposit(currentLoggedInAccountId, amountToDeposit);
        atmMachine.receiveMoney(amountToDeposit);
        return "Current balance: " + transactionRecord.balance;
    }

    public String balance() {
        return "Current balance: " + atmService.balance(currentLoggedInAccountId);
    }

    public String history() {
        List<TransactionRecord> history = atmService.history(currentLoggedInAccountId);
        if (history.size() == 0) {
            return "No history found.";
        }

        StringBuilder stringBuilder = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");

        for (TransactionRecord record : history) {
            stringBuilder.append(
                    formatter.format(Instant.ofEpochMilli(record.timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()));
            stringBuilder.append(" ");
            stringBuilder.append(record.amount);
            stringBuilder.append(" ");
            stringBuilder.append(record.balance);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public void setLastInteractionTimestamp(long lastInteractionTimestamp) {
        this.lastInteractionTimestamp = lastInteractionTimestamp;
    }
}
