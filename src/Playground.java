

public class Playground {

    // The account balance
    private static int balance;

    // Deposit money into the account
    public static int deposit(int amount) {
        balance = balance + amount;
        return amount;
    }

    // Withdraw money from the account
// Restriction: account - amount must be positive.
    public static int withdraw(int amount) {
        if (balance - amount >= 0) {
            amount = -1 * amount;
            deposit(amount);
            return -1 * amount;
        } else {
            return -1;
        }
    }

    // Computes the number of units of unit_size to dispense for amount.
    public static int units(int amount, int unit_size) {
        return amount / unit_size;
    }

    // Dispenses the amount
    public static void dispense(int amount) {
        int hundreds; int fifties; int twenties; int tens;
        int fives; int ones;
        hundreds = 100; fifties = 50; twenties = 20; tens = 10; fives = 5; ones = 1;

        hundreds = units(amount, hundreds);
        amount   = amount % 100;

        fifties  = units(amount, fifties);
        amount   = amount % 50;

        twenties = units(amount, twenties);
        amount   = amount % 20;

        tens     = units(amount, tens);
        amount   = amount % 10;

        fives    = units(amount, fives);
        amount   = amount % 5;

        ones     = units(amount, ones);

        IO.print_s("********* Dispensing *********\n");
        IO.print_i(hundreds); IO.print_s("x100 bills\n");
        IO.print_i(fifties);  IO.print_s("x 50 bills\n");
        IO.print_i(twenties); IO.print_s("x 20 bills\n");
        IO.print_i(tens);     IO.print_s("x 10 bills\n");
        IO.print_i(fives);    IO.print_s("x  5 bills\n");
        IO.print_i(ones);     IO.print_s("x  1 coins\n");
        IO.print_s("******************************\n");
    }

    // Select either withdraw or deposit.
    public static int select_service() {
        int service;
        IO.print_s("SERVICES:\n");
        IO.print_s("1: Withdraw\n");
        IO.print_s("2: Deposit\n");
        IO.print_s("Enter service number> ");
        service = IO.read_i();
        IO.read_c(); // consume enter
        return service;
    }

    // Use a particular service
    public static void use_service(int service) {
        int amount; int status;
        if (service < 1) IO.print_s("error: Invalid service.\n");
        else if (service > 2) IO.print_s("error: Invalid service.\n");
        else {
            if (service == 1) {
                IO.print_s("info: Service 'Withdraw' selected\n");
                IO.print_s("Your current balance is: "); IO.print_i(balance);IO.print_s("\n");
                IO.print_s("Enter amount to withdraw> ");
                amount = IO.read_i();
                IO.read_c(); // consume enter
                if (amount >= 0) {
                    status = withdraw(amount);
                    if (status < 0) {
                        IO.print_s("error: You got an insufficient balance. Your basic account does not allow overdrafts.\n");
                        IO.print_s("       Consider upgrading to a premium account.\n");
                    } else {
                        dispense(amount);
                    }
                } else {
                    IO.print_s("error: You cannot withdraw a negative amount!\n");
                }
            } else {
                IO.print_s("info: Service 'Deposit' selected\n");
                IO.print_s("Your current balance is: ");IO.print_i(balance);IO.print_s("\n");
                IO.print_s("Enter amount to deposit> ");
                amount = IO.read_i();
                IO.read_c(); // consume enter
                if (amount <= 0) {
                    IO.print_s("error: Cannot deposit non-positive amount.\n");
                } else {
                    status = deposit(amount);
                    if (status < 0) {
                        IO.print_s("error: Could not deposit money, please try again.\n");
                    } else {
                        IO.print_s("Successfully deposited ");IO.print_i(amount);IO.print_s(".\n");
                    }
                }
            }
        }
    }

    // Ask whether to the user wishes to do another transaction
    public static int new_transaction() {
        char yesno;
        IO.print_s("Do you wish to carry out another transaction? (y/n)> ");
        yesno = IO.read_c();
        IO.read_c(); // consume enter
        if (yesno == 'y') return 1;
        else if (yesno == 'Y') return 1;
        else return 0;
    }

    // Program entry point
    void main() {
        int transaction; int service;

        balance = 1000;  // Initial balance
        transaction = 1; // Indicates a session is ongoing

        while (transaction > 0) { // while in a session
            service = select_service();
            use_service(service);
            transaction = new_transaction();
        }
    }


    public static void main(String[] args) {
        int i = 40000;
        int n = i << 15;

        IO.print_i(n);
    }

}
