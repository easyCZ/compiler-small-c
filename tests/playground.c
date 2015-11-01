#include "io.h"
#include "io.c"

int t;
char s;
void m;

int func1( int a, char v ) {
  char a;
  a = a+1;
  v = v*5/7%8+1+(-5) +func(a)+func(a,v)*3 - 'c';
  return v;
}
int func( int a) {
  int v;

  a = a+1;
  v = v*5/7%8+1-5 +func(a)+func(a,v)*3;
  return v;
}


char sad() {
  int a;
  char v;
  char b;
  int n;
  a = a+1;
  v = read_c();
  print_c(a%4+b/2-n*3+'c'*5+'d');
  print_i(8);
  return;
}

void main() {
  int n;
  int first;
  int second;
  int next;
  int c;
  char t;
  char a;
  int g;
  char d;
  char e;

  // read n from the standard input
  n = read_i();
  
  first = 0;
  second = 1;
    
  print_s("First ");
  print_i(n);
  print_s(" terms of Fibonacci series are : ");
 
  c = 0;
  while (c == n) {
    int c; char d; void m; 
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
  read_c();
  if(t<=4){ if (a-1) a=5; else a=10; }
  if(t>3) if(g>=4) a=5; else a=6; else a=7;
  while(5 != 5) read_c();
  c = -5*-4;
  d = ('c'*-9);
  func(c,d,e);
  func(c);
  func();
  return;
  return 55;
}