package app;

import banking.AtmService;
import banking.FakeTimeSource;
import banking.TransactionRecord;
import machine.AtmMachine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class AtmCoreTest {

    private AtmCore atmCore;
    private AtmService atmService;
    private AtmMachine atmMachine;
    private FakeTimeSource timeSource;

    private final String accountId = "testAccountId";
    private final String accountPin = "testAccountPin";

    @Before
    public void setUp() {
        timeSource = new FakeTimeSource();
        atmService = Mockito.mock(AtmService.class);
        atmMachine = Mockito.mock(AtmMachine.class);
        atmCore = new AtmCore(timeSource, atmService, atmMachine);
    }

    @Test
    public void testLoginSuccess() {
        Mockito.when(atmService.authorize(accountId, accountPin)).thenReturn(true);
        Assert.assertTrue(atmCore.login(accountId, accountPin).contains(accountId + " successfully authorized"));
        Assert.assertTrue(atmCore.hasLogin());
    }

    @Test
    public void testLoginFailure() {
        Mockito.when(atmService.authorize(accountId, accountPin)).thenReturn(false);
        Assert.assertTrue(atmCore.login(accountId, accountPin).contains("Authorization failed."));
        Assert.assertFalse(atmCore.hasLogin());
    }

    @Test
    public void testLoginTimeout() {
        Mockito.when(atmService.authorize(accountId, accountPin)).thenReturn(true);
        Assert.assertTrue(atmCore.login(accountId, accountPin).contains(accountId + " successfully authorized"));
        Assert.assertTrue(atmCore.hasLogin());
        timeSource.incrementTimeMillis(2 * 60 * 1000 - 1);
        Assert.assertTrue(atmCore.hasLogin());
        timeSource.incrementTimeMillis(1);
        Assert.assertFalse(atmCore.hasLogin());
    }

    @Test
    public void testLogoutSuccess() {
        Mockito.when(atmService.authorize(accountId, accountPin)).thenReturn(true);
        atmCore.login(accountId, accountPin);
        Assert.assertTrue(atmCore.logout().contains("Account " + accountId + " logged out."));
    }

    @Test
    public void testLogoutFailure() {
        Assert.assertTrue(atmCore.logout().contains("No account is currently authorized."));
    }

    @Test
    public void testWithdrawalNotMultipleOfTwenty() {
        Assert.assertTrue(atmCore.withdraw(19).contains("Only multiples of 20 may be withdrawn."));
    }

    @Test
    public void testWithdrawalOverdrawn() {
        Mockito.when(atmService.balance(accountId)).thenReturn(0.);
        Assert.assertTrue(atmCore.withdraw(20).contains("Your account is overdrawn! You may not make withdrawals at this time."));
    }

    @Test
    public void testWithdrawalMachineEmpty() {
        // log in
        Mockito.when(atmService.authorize(accountId, accountPin)).thenReturn(true);
        atmCore.login(accountId, accountPin);

        int amountRequested = 20;
        Mockito.when(atmService.balance(accountId)).thenReturn((double) amountRequested);
        Mockito.when(atmMachine.getDispensibleAmountOfTargetValue(amountRequested)).thenReturn(0);

        Assert.assertTrue(atmCore.withdraw(amountRequested).contains("Unable to process your withdrawal at this time."));
    }

    @Test
    public void testWithdrawalPartialDispenseNoOverdraft() {
        String withdrawalResponseMessage = doSuccessfulWithdrawal(40, 20, 0, 0);

        Assert.assertTrue(withdrawalResponseMessage.contains("Unable to dispense full amount requested at this time.\n"));
        Assert.assertTrue(withdrawalResponseMessage.contains("Amount dispensed: 20.0"));
        Assert.assertTrue(withdrawalResponseMessage.contains("Current balance: 0.0"));
        Assert.assertFalse(withdrawalResponseMessage.contains("You have been charged an overdraft fee"));
    }

    @Test
    public void testWithdrawalPartialDispenseWithOverdraft() {
        String withdrawalResponseMessage = doSuccessfulWithdrawal(20, 10, -15, 5);

        Assert.assertTrue(withdrawalResponseMessage.contains("Unable to dispense full amount requested at this time.\n"));
        Assert.assertTrue(withdrawalResponseMessage.contains("Amount dispensed: 10.0"));
        Assert.assertTrue(withdrawalResponseMessage.contains("Current balance: -15.0"));
        Assert.assertTrue(withdrawalResponseMessage.contains("You have been charged an overdraft fee of $5"));
    }

    @Test
    public void testWithdrawalFullDispenseNoOverdraft() {
        String withdrawalResponseMessage = doSuccessfulWithdrawal(20, 20, 20, 0);

        Assert.assertFalse(withdrawalResponseMessage.contains("Unable to dispense full amount requested at this time.\n"));
        Assert.assertTrue(withdrawalResponseMessage.contains("Amount dispensed: 20.0"));
        Assert.assertTrue(withdrawalResponseMessage.contains("Current balance: 20.0"));
        Assert.assertFalse(withdrawalResponseMessage.contains("You have been charged an overdraft fee"));
    }

    @Test
    public void testWithdrawalFullDispenseWithOverdraft() {
        String withdrawalResponseMessage = doSuccessfulWithdrawal(20, 20, -15, 5);

        Assert.assertFalse(withdrawalResponseMessage.contains("Unable to dispense full amount requested at this time.\n"));
        Assert.assertTrue(withdrawalResponseMessage.contains("Amount dispensed: 20.0"));
        Assert.assertTrue(withdrawalResponseMessage.contains("Current balance: -15.0"));
        Assert.assertTrue(withdrawalResponseMessage.contains("You have been charged an overdraft fee of $5"));
    }

    @Test
    public void testDepositSuccessful() {
        // log in
        Mockito.when(atmService.authorize(accountId, accountPin)).thenReturn(true);
        atmCore.login(accountId, accountPin);

        int amountToDeposit = 20;
        int newBalance = 40;
        Mockito.when(atmService.deposit(accountId, amountToDeposit)).thenReturn(
                new TransactionRecord(0, amountToDeposit, newBalance, 0));

        Assert.assertTrue(atmCore.deposit(amountToDeposit).contains("Current balance: " + newBalance));
    }

    @Test
    public void testDepositZero() {
        // log in
        Mockito.when(atmService.authorize(accountId, accountPin)).thenReturn(true);
        atmCore.login(accountId, accountPin);

        Assert.assertTrue(atmCore.deposit(0).contains("Can only deposit positive amounts of money."));
    }

    @Test
    public void testDepositNegative() {
        // log in
        Mockito.when(atmService.authorize(accountId, accountPin)).thenReturn(true);
        atmCore.login(accountId, accountPin);

        Assert.assertTrue(atmCore.deposit(0).contains("Can only deposit positive amounts of money."));
    }

    @Test
    public void testBalance() {
        // log in
        Mockito.when(atmService.authorize(accountId, accountPin)).thenReturn(true);
        atmCore.login(accountId, accountPin);

        double balance = 20;

        Mockito.when(atmService.balance(accountId)).thenReturn(balance);
        Assert.assertTrue(atmCore.balance().contains("Current balance: " + balance));
    }

    private String doSuccessfulWithdrawal(int amountRequested, int amountDispensible, double balanceAfterTransaction, double overdraftFee) {
        // log in
        Mockito.when(atmService.authorize(accountId, accountPin)).thenReturn(true);
        atmCore.login(accountId, accountPin);

        Mockito.when(atmService.balance(accountId)).thenReturn(10.);
        Mockito.when(atmMachine.getDispensibleAmountOfTargetValue(amountRequested)).thenReturn(amountDispensible);

        Mockito.when(atmService.withdraw(accountId, amountDispensible)).thenReturn(
                new TransactionRecord(Mockito.anyLong(), amountDispensible, balanceAfterTransaction, overdraftFee));
        Mockito.when(atmMachine.dispenseMoney(amountDispensible)).thenReturn(amountDispensible);

        return atmCore.withdraw(amountRequested);
    }
}