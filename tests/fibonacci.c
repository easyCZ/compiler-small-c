#include "io.h"

int variable1;
char variable2;

void main() {
  int n;
  int first;
  int second;
  int next;
  int c;
  char t;

  // read n from the standard input
  /*
    This is a multiline comment
  */
  n = read_i();

  first = 0;
  second = 1;

  print_s("First ");
  print_i(n);
  print_s(" terms of Fibonacci series are : ");

  c = 0;
  while (c < n) {
    if ( c <= 1 )
      next = c;
    else
      {
	next = first + second;
	first = second;
	second = next;
      }
    print_i(next);
    print_s(" ");
    c = c+1;
  }
}
