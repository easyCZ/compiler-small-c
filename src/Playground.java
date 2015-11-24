

public class Playground {

    public static int value;

    public static int test(int i) {
//        value = 1;
        {
//            int i;
            i = 17;
//            i = a + value;
        }
        {
            int b;
            b = 24;
            i = i + b;
        }

        return i;
    }



    public static void main(String[] args) {
        int i = Playground.test(1);
        IO.print_i(i);
        IO.print_s("Hello World!\n");
        IO.print_c('c');
    }

}
