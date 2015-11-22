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
        int j;

        if (i == 67) {
            j = 1 + 1;
        }
        else {
            j = 2 + 2;
        }
    }


    public static void main(String[] args) {
        int i = 1 + 1;
        System.out.println(i);
    }

}
