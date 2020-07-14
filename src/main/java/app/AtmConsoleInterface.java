package app;

import java.io.Console;

/**
 * the AtmApp class represents the GUI application (or in this case, console application) running on the ATM for users
 */
public class AtmConsoleInterface {

    public static void main(String[] args) {
        AtmCore atmCore = new AtmCore();
        Console console = System.console();
        String[] command = console.readLine("Enter a command (enter \"help\" for help): ").split(" ");
        while (!command[0].equals("end")) {

            if (!command[0].equals("logout") && !atmCore.hasLogin()) {
                System.out.println("Authorization required.");
                return;
            }

            switch (command[0]) {
                case "authorize":
                    if (command.length != 3) {
                        System.out.println("usage: authorize <account_id> <pin>");
                    } else {
                        System.out.println(atmCore.login(command[1], command[2]));
                    }
                    break;
                case "withdraw":
                    if (command.length != 2) {
                        System.out.println("usage: withdraw <integer value>");
                    } else {
                        try {
                            int amountToWithdraw = Integer.parseInt(command[1]);
                            System.out.println(atmCore.withdraw(amountToWithdraw));
                        } catch (NumberFormatException e) {
                            System.out.println("\"" + command[1] + "\"" + "not parsable as integer");
                        }
                    }
                    break;
                case "deposit":
                    // attempt deposit
                    break;
                case "balance":
                    // attempt balance
                    break;
                case "history":
                    // attempt history
                    break;
                case "logout":
                    if (command.length != 1) {
                        System.out.println("usage: logout");
                    } else {
                        System.out.println(atmCore.logout());
                    }
                    break;
                default:
                    System.out.println("Command not recognized");
            }
            atmCore.setLastInteractionTimestamp(System.currentTimeMillis());
            console.readLine("Enter a command (enter \"help\" for help): ");
        }
    }
}