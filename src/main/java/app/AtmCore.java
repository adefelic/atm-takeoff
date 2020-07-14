package app;

import banking.AtmService;
import banking.TransactionRecord;
import machine.AtmMachine;

/**
 * AtmCore holds business logic that uses both remote banking services (AtmService) and local machine information (AtmMachine)
 */
public class AtmCore {

    private static final long LOGIN_TIMEOUT_MS = 1000 * 60 * 2;
    private final AtmService atmService = new AtmService();
    private final AtmMachine atmMachine = new AtmMachine(10000);
    private String currentLoggedInAccountId; // this is set to null if there isn't an active login
    private long lastInteractionTimestamp;

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
        return currentLoggedInAccountId != null && lastInteractionTimestamp + LOGIN_TIMEOUT_MS < System.currentTimeMillis();
    }

    public String withdraw(int requestedAmount) {
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
            stringBuilder.append(transactionRecord.amount);
            stringBuilder.append("\n");
            if (transactionRecord.overdraftFee > 0) {
                stringBuilder.append("You have been charged an overdraft fee of $");
                stringBuilder.append(transactionRecord.overdraftFee);
                stringBuilder.append(" ");
            }
            stringBuilder.append("Current balance: ");
            stringBuilder.append(transactionRecord.balance);
            stringBuilder.append("\n");

            return stringBuilder.toString();
        }
        return "No transaction (something went wrong in the AtmService)";
    }

    public void deposit(double value) {
        //todo
    }
    public void balance() {
        //todo
    }
    public void history() {
        //todo
    }

    public void setLastInteractionTimestamp(long lastInteractionTimestamp) {
        this.lastInteractionTimestamp = lastInteractionTimestamp;
    }
}
