#include "io.h"

int k;


int test(int i, int j) {
k = 25;
    {
        int i;

        i = 10;
        {
            int i;
            i = 17;
        }
    }
    {
        i = i + 5;
    }
    k = 1;
    return i + k;
}

void main() {
    int a;
    int b;
    int c;

    k = 17;

    a = 1;
    b = 2;
    c = 1 + test(a, b);


}
