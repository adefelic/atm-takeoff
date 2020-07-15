package app;

import banking.AtmService;
import machine.AtmMachine;

import java.io.Console;

/**
 * the AtmApp class represents the GUI application (or in this case, console application) running on the ATM for users
 */
public class AtmConsoleInterface {

    public static void main(String[] args) {
        ITimeSource timeSource = new TimeSource();
        AtmCore atmCore = new AtmCore(timeSource, new AtmService(timeSource), new AtmMachine(10000));
        Console console = System.console();

        String[] command = promptForCommand(console);
        while (!command[0].equals("end")) {
            switch (command[0]) {
                case "authorize":
                    attemptAuthorize(command, atmCore);
                    break;
                case "withdraw":
                    attemptWithdraw(command, atmCore);
                    break;
                case "deposit":
                    attemptDeposit(command, atmCore);
                    break;
                case "balance":
                    attemptBalance(command, atmCore);
                    break;
                case "history":
                    attemptHistory(command, atmCore);
                    break;
                case "logout":
                    attemptLogout(command, atmCore);
                    break;
                case "help":
                    String commands = "commands:\n" +
                            "  authorize <account_id> <pin>\n" +
                            "  withdraw <integer value>\n" +
                            "  deposit <integer value>\n" +
                            "  balance\n" +
                            "  history\n" +
                            "  logout";
                    System.out.println(commands);
                default:
                    System.out.println("Command not recognized");
            }
            atmCore.setLastInteractionTimestamp(System.currentTimeMillis());
            command = promptForCommand(console);
        }
    }

    private static String[] promptForCommand(Console console) {
        return console.readLine("Enter a command (enter \"help\" for help): ").split(" ");
    }

    private static void attemptAuthorize(String[] command, AtmCore atmCore) {
        if (command.length != 3) {
            System.out.println("usage: authorize <account_id> <pin>");
        } else {
            System.out.println(atmCore.login(command[1], command[2]));
        }
    }

    private static void attemptWithdraw(String[] command, AtmCore atmCore) {
        if (!atmCore.hasLogin()) {
            System.out.println("Authorization required.");
            return;
        }
        if (command.length != 2) {
            System.out.println("usage: withdraw <integer value>");
            return;
        }
        try {
            int amountToWithdraw = Integer.parseInt(command[1]);
            System.out.println(atmCore.withdraw(amountToWithdraw));
        } catch (NumberFormatException e) {
            System.out.println("\"" + command[1] + "\"" + "not parsable as integer");
        }
    }

    private static void attemptDeposit(String[] command, AtmCore atmCore) {
        if (!atmCore.hasLogin()) {
            System.out.println("Authorization required.");
            return;
        }
        if (command.length != 2) {
            System.out.println("usage: deposit <integer value>");
            return;
        }
        try {
            int amountToDeposit = Integer.parseInt(command[1]);
//            System.out.println(atmCore.deposit(amountToDeposit));
        } catch (NumberFormatException e) {
            System.out.println("\"" + command[1] + "\"" + "not parsable as integer");
        }
    }

    private static void attemptBalance(String[] command, AtmCore atmCore) {
        if (!atmCore.hasLogin()) {
            System.out.println("Authorization required.");
            return;
        }
        if (command.length != 1) {
            System.out.println("usage: balance");
            return;
        }
//        System.out.println(atmCore.balance());
    }

    private static void attemptHistory(String[] command, AtmCore atmCore) {
        if (!atmCore.hasLogin()) {
            System.out.println("Authorization required.");
            return;
        }
        if (command.length != 1) {
            System.out.println("usage: history");
            return;
        }
//        System.out.println(atmCore.history());
    }

    private static void attemptLogout(String[] command, AtmCore atmCore) {
        if (command.length != 1) {
            System.out.println("usage: logout");
            return;
        }
        System.out.println(atmCore.logout());
    }
}
