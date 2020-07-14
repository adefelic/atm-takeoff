package machine;

/**
 * Represents the state of the physical ATM machine and its money reserves
 */
public class AtmMachine {
    static final int MAX_DOLLARS_IN_MACHINE = 20000; // there has to be a max
    private int dollarsInMachine;

    public AtmMachine(int dollarsInMachine) {
        // in real life this value is sensed or read from persistence
        // parameterized for testing
        this.dollarsInMachine = dollarsInMachine;
    }

    /**
     * @return if operation was successful
     */
    public boolean receiveMoney(int dollarsToReceive) {
        if (canReceiveAmount(dollarsToReceive)) {
            dollarsInMachine += dollarsToReceive;
            return true;
        }
        return false;
    }

    /**
     * dispenses the dollar value requested.
     * if only partial funds are available in the machine, dispenses that much instead.
     *
     * note: the machine only dispensing 20s is handled in the application layer rather than the machine abstraction,
     * as i'm not sure if it's a requirement of the software or of the machine (i'm deciding it's a req of the software)
     *
     * @return the amount dispensed
     */
    public int dispenseMoney(int targetDollarValue) {
        if (targetDollarValue < 0) {
            return 0;
        }
        int dispensibleAmount = getDispensibleAmountOfTargetValue(targetDollarValue);
        dollarsInMachine -= dispensibleAmount;
        return dispensibleAmount;
    }

    /**
     * @param targetDollarValue
     * @return the amount of dollars the machine can dispense, up to dollarsToDispense
     */
    public int getDispensibleAmountOfTargetValue(int targetDollarValue) {
        if (dollarsInMachine - targetDollarValue < 0.) {
            return dollarsInMachine;
        }
        return targetDollarValue;
    }

    public boolean canReceiveAmount(int dollarsToReceive) {
        return dollarsInMachine + dollarsToReceive <= MAX_DOLLARS_IN_MACHINE;
    }

    // visible for testing (google has an annotation for this that i'm not importing)
    public int getDollarsInMachine() {
        return dollarsInMachine;
    }
}
