#include "io.h"


int test() {
    if (10 < 1) return 1;
    else return 2;
}

void main() {
    int i;
    int j;

    i = 0;
    j = 9;

    while (i * j <= 99) {
        i = i + 1;
        j = i;
    }



}
