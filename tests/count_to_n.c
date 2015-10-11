#include "io.h"

int increment(int i) {
    return i + 1;
}


void main() {
    int n;
    int i;
    n = read_i();

    i = 1;

    while (i < n) {
        i = increment(i);
        print_i(i);
    }

}

