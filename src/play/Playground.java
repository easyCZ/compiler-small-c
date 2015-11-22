package play;


public class Playground {

    public int lonely() {
        int k;
        char c;
        String str;

        k = 99;
        c = 'a';
        str = "hello";

//        boolean bool = k == 9;

        if (true) {
//            k = 10;
        }
        else {
            k = 10;
        }

        return k + 7;
    }

    public int test(int i, int x) {
        if (i == 1) x = 1;
        else x = 2;
        return x;
    }

    public static void play(int i) {

        if (i == 67) {
            i = 10;
        }
    }

    public static void bools(int i) {
        boolean b;
        int j;

        if (127 + i == 99) b = true;
        else b = false;

        if (b) j = 98;
        else j = 100;
    }


    public static void main(String[] args) {
        int i = 1 + 1;
        System.out.println(i);
    }

}
