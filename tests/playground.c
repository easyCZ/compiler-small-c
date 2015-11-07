#include "io.h"

int iteration_counter;
int count;

int factorial(int n) {
  int smaller;

  iteration_counter = iteration_counter + 1;
  if (n == 1) return 1;
  else {
    smaller = n - 1;
    return n * factorial(smaller);
  }
}

void print_fac(int n) {
  int fac;

  fac = factorial(n);

  print_s("(: ");
  print_i(fac);
  print_s(" :)\n");
  print_c('a');
}

int shadow(int a, int iteration_counter) {
  count = count + 1;
  print_i(a);
  print_i(iteration_counter);
}

void main() {
  count = 15;

  if (read_i()) {
    print_fac(count);
  }


}
