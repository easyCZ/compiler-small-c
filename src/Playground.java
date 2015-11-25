

public class Playground {

    public static void main(String[] args) {
        IO.print_s("Enter year> ");
        int var1 = IO.read_i();
        IO.print_i(var1);
        if(var1 % 400 == 0) {
            IO.print_s(" is a leap year.\n");
        } else if(var1 % 100 == 0) {
            IO.print_s(" is not a leap year.\n");
        } else if(var1 % 4 == 0) {
            IO.print_s(" is a leap year.\n");
        } else {
            IO.print_s(" is not a leap year.\n");
        }
    }

}
