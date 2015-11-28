

public class Playground {


    public static void main(String[] args) {
        int i = IO.read_i();
        int upper = i >> 16;
        int lower = i & 0x0000FFFF;

        int result = (upper << 16) + lower;

        IO.print_i(upper);
        IO.print_s("/n");
        IO.print_i(lower);
        IO.print_s("/n");
    }

}
