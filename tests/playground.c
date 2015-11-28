#include "io.h"

void shadow(int a) {
  if (a > 0) {

    if (a == 1) {
        print_s("1");
    }
    else if (a == 2) {
        print_s("2");
    }
    else if (a == 3) {
        print_s("3");
    }
    else {
        if (a == 4) {
            print_s("4");
        }
        else {
            print_i(a);
        }

    }

  }
}

void main() {
    int a;

    print_s("Get int > ");
    a = read_i();
    shadow(a);
}
