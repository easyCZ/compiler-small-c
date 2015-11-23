#include "io.h"


int test(int i, int j) {
    {


        i = 10;
    }
    {
        int i;
        i = 5;
    }
    return i + j;
}

void main() {
    int a;
    int b;
    int c;

    a = 1;
    b = 2;
    c = 1 + test(a, b);


}
