package machine;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AtmMachineTest {

    AtmMachine atmMachine;

    @Before
    public void before() {
        atmMachine = new AtmMachine(100);
    }


    @Test
    public void testCanReceiveAmount() {
        Assert.assertEquals(100, atmMachine.getDollarsInMachine());
        Assert.assertTrue(atmMachine.canReceiveAmount(100));
        Assert.assertTrue(atmMachine.canReceiveAmount(19900));
        Assert.assertFalse(atmMachine.canReceiveAmount(19901));
    }

    @Test
    public void testReceiveMoney() {
        Assert.assertEquals(100, atmMachine.getDollarsInMachine());
        Assert.assertTrue(atmMachine.receiveMoney(100));
    }

    @Test
    public void testDispenseMoney() {
        Assert.assertEquals(100, atmMachine.getDollarsInMachine());
        Assert.assertEquals(0, atmMachine.dispenseMoney(0));

        Assert.assertEquals(100, atmMachine.getDollarsInMachine());
        Assert.assertEquals(0, atmMachine.dispenseMoney(-1));

        Assert.assertEquals(100, atmMachine.getDollarsInMachine());
        Assert.assertEquals(20, atmMachine.dispenseMoney(20));

        Assert.assertEquals(80, atmMachine.getDollarsInMachine());
        Assert.assertEquals(80, atmMachine.dispenseMoney(100));

        Assert.assertEquals(0, atmMachine.getDollarsInMachine());
    }

    @Test
    public void testGetDispensibleAmount() {
        Assert.assertEquals(0, atmMachine.getDispensibleAmountOfTargetValue(0));
        Assert.assertEquals(50, atmMachine.getDispensibleAmountOfTargetValue(50));
        Assert.assertEquals(100, atmMachine.getDispensibleAmountOfTargetValue(100));
        Assert.assertEquals(100, atmMachine.getDispensibleAmountOfTargetValue(110));
    }

}