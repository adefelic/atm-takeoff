package banking;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AtmServiceTest {

    AtmService atmService;

    @Before
    public void setUp() {
        atmService = new AtmService(new FakeTimeSource());
        // for the time being, AtmService is hardcoded to initialize with the following accounts:
        //   Account("2859459814", "7386", 10.24)
        //   Account("1434597300", "4557", 90000.55)
        //   Account("7089382418", "0075", .0)
        //   Account("2001377812", "5950", 60.)
    }

    @Test
    public void testAuthorizeSuccess() {
        Assert.assertTrue(atmService.authorize("2859459814", "7386"));
    }

    @Test
    public void testAuthorizeAccountIdDoesNotExist() {
        Assert.assertFalse(atmService.authorize("testId", "7386"));
    }

    @Test
    public void testAuthorizeIncorrectPin() {
        Assert.assertFalse(atmService.authorize("2859459814", "incorrectPin"));
    }

    @Test
    public void testWithdrawSuccessfulWithdrawal() {
        // atmService has the following account: Account(id: "2001377812", pin: "5950", balance: 60.)
        String accountId = "2001377812";
        Assert.assertEquals(60., atmService.balance(accountId), 0);
        Assert.assertEquals(
                new TransactionRecord(0, 20, 40,0),
                atmService.withdraw(accountId, 20));
        Assert.assertEquals(40., atmService.balance(accountId), 0);
    }

    @Test
    public void testWithdrawSuccessfulWithdrawalAllFunds() {
        // atmService has the following account: Account(id: "2001377812", pin: "5950", balance: 60.)
        String accountId = "2001377812";
        Assert.assertEquals(
                new TransactionRecord(0, 60, 0,0),
                atmService.withdraw(accountId, 60));
        Assert.assertEquals(0, atmService.balance(accountId), 0);

    }

    @Test
    public void testWithdrawSuccessfulWithdrawalWithOverdraw() {
        // atmService has the following account: Account(id: "2001377812", pin: "5950", balance: 60.)
        String accountId = "2001377812";
        Assert.assertEquals(
                new TransactionRecord(0, 80, -25,5),
                atmService.withdraw(accountId, 80));
        Assert.assertEquals(-25, atmService.balance(accountId), 0);

    }

    @Test
    public void testWithdrawUnsuccessfulWithdrawalNoFunds() {
        // atmService has the following account: Account(id: "2001377812", pin: "5950", balance: 60.)
        String accountId = "2001377812";
        // empty the account
        Assert.assertEquals(
                new TransactionRecord(0, 60, 0,0),
                atmService.withdraw(accountId, 60));

        // now fail to withdraw
        Assert.assertEquals(
                TransactionRecord.NO_TRANSACTION,
                atmService.withdraw(accountId, 10));
    }

    @Test
    public void testWithdrawNegativeAmount() {
        // atmService has the following account: Account(id: "2001377812", pin: "5950", balance: 60.)
        String accountId = "2001377812";
        Assert.assertEquals(
                TransactionRecord.NO_TRANSACTION,
                atmService.withdraw(accountId, -60));
    }

    @Test
    public void testWithdrawZero() {
        // atmService has the following account: Account(id: "2001377812", pin: "5950", balance: 60.)
        String accountId = "2001377812";
        Assert.assertEquals(
                TransactionRecord.NO_TRANSACTION,
                atmService.withdraw(accountId, 0));
    }

    @Test
    public void testWithdrawInvalidAccount() {
        // atmService has the following account: Account(id: "2001377812", pin: "5950", balance: 60.)
        String accountId = "2001377812";
        Assert.assertEquals(
                TransactionRecord.NO_TRANSACTION,
                atmService.withdraw(accountId, -60));
    }

    @Test
    public void testDepositZero() {
        String accountId = "2001377812";
        Assert.assertEquals(
                TransactionRecord.NO_TRANSACTION,
                atmService.deposit(accountId, 0));
    }

    @Test
    public void testDepositNegative() {
        String accountId = "2001377812";
        Assert.assertEquals(
                TransactionRecord.NO_TRANSACTION,
                atmService.deposit(accountId, -10));
    }

    @Test
    public void testDepositSuccessful() {
        String accountId = "2001377812";
        Assert.assertEquals(60., atmService.balance(accountId), 0);
        Assert.assertEquals(
                new TransactionRecord(0, 20, 80,0),
                atmService.deposit(accountId, 20));
        Assert.assertEquals(80., atmService.balance(accountId), 0);
    }
}