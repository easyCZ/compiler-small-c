package lexer;

import util.Position;

import java.util.Arrays;
import java.util.List;

/**
 * @author cdubach
 */
public class Token {

    public static final int DUMMY_LINE = -1;
    public static final int DUMMY_COL = -1;

    public enum TokenClass {

        IDENTIFIER, // ('a'|...|'z'|'A'|...|'Z'|'_')('0'|...|'9'|'a'|...|'z'|'A'|...|'Z'|'_')*

        ASSIGN, // '='

        // delimiters
        LBRA, // '{'
        RBRA, // '}'
        LPAR, // '('
        RPAR, // ')'
        SEMICOLON, // ';'
        COMMA, // ','

        // special functions
        MAIN,  // "main"
        PRINT, // "print_s" | "print_i" | "print_c"
        READ,  // "read_i" | "read_c"

        // types
        INT,  // "int"
        VOID, // "void"
        CHAR, // "char"

        // control flow
        IF,     // "if"
        ELSE,   // "else"
        WHILE,  // "while"
        RETURN, // "return"

        // include
        INCLUDE, // "#include"

        // literals
        STRING_LITERAL, // \".*\"  any sequence of characters enclosed in double quote " (please be aware of the escape character backslash \)
        NUMBER,         // ('0'|...|'9')+
        CHARACTER,      // \'('a'|...|'z'|'A'|...|'Z'|'\t'|'\n'|'.'|','|'_'|...)\'  a character starts and end with a single quote '

        // comparisons
        EQ, // "=="
        NE, // "!="
        LT, // '<'
        GT, // '>'
        LE, // "<="
        GE, // ">="

        // arithmetic operators
        PLUS,  // '+'
        MINUS, // '-'
        TIMES, // '*'
        DIV,   // '/'
        MOD,   // '%'

        // special tokens
        EOF,    // signal end of file
        INVALID // in case we cannot recognise a character as part of a valid token
    }

    public static final List<TokenClass> TYPES = Arrays.asList(
            TokenClass.INT,
            TokenClass.VOID,
            TokenClass.CHAR
    );

    public static final List<TokenClass> COMPARATORS = Arrays.asList(
            TokenClass.GT,
            TokenClass.LT,
            TokenClass.GE,
            TokenClass.LE,
            TokenClass.NE,
            TokenClass.EQ
    );

    public static final String PRINT_S = "print_s";
    public static final String PRINT_C = "print_c";
    public static final String PRINT_I = "print_i";


    public final TokenClass tokenClass;
    public final String data;
    public final Position position;

    public Token(TokenClass type, int lineNum, int colNum) {
        this(type, "", lineNum, colNum);
    }

    public Token (TokenClass tokenClass, String data, int lineNum, int colNum) {
        assert (tokenClass != null);
        this.tokenClass = tokenClass;
        this.data = data;
        this.position = new Position(lineNum, colNum);
    }

    public Token(TokenClass tokenClass) {
        this(tokenClass, "", DUMMY_LINE, DUMMY_COL);
    }

    public Token(TokenClass tokenClass, String data) {
        this(tokenClass, data, DUMMY_LINE, DUMMY_COL);
    }



    @Override
    public String toString() {
        if (data.equals(""))
            return tokenClass.toString();
        else
            return tokenClass.toString()+"("+data+")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Token) {
            Token t = (Token) obj;
            return t.data.equals(this.data);
                // &&
                // t.tokenClass.equals(this.tokenClass) &&
                // t.position.toString().equals(this.position.toString());
        }
        return false;
    }

    /*
        Test if the current token is a type
     */
    public boolean isType() {
        return this.tokenClass == TokenClass.CHAR ||
                this.tokenClass == TokenClass.INT ||
                this.tokenClass == TokenClass.VOID;
    }

}

