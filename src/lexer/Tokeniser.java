package lexer;

import lexer.Token.TokenClass;
import lexer.tokens.AbstractToken;
import lexer.tokens.SimpleToken;
import lexer.tokens.StringLiteral;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cdubach
 */
public class Tokeniser {

    final String INCLUDE = "include";
    final List<Character> delimiters;

    private Scanner scanner;
    private final TokeniserService service;

    private int error = 0;
    public int getErrorCount() {
	return this.error;
    }

    private final List<AbstractToken> matchers;

    public Tokeniser(Scanner scanner) {
        this.scanner = scanner;
        this.service = new TokeniserService(scanner);

        this.delimiters = new ArrayList<Character>();
        delimiters.add('{');
        delimiters.add('}');
        delimiters.add('(');
        delimiters.add(')');
        delimiters.add(';');
        delimiters.add(',');


        this.matchers = new ArrayList<>();

        matchers.add(new StringLiteral('"', scanner));

        addSimpleMatcher('+', TokenClass.PLUS);
        addSimpleMatcher(';', TokenClass.SEMICOLON);
        addSimpleMatcher(',', TokenClass.COMMA);
        addSimpleMatcher('(', TokenClass.LPAR);
        addSimpleMatcher(')', TokenClass.RPAR);
        addSimpleMatcher('{', TokenClass.LBRA);
        addSimpleMatcher('}', TokenClass.RBRA);
        addSimpleMatcher('=', TokenClass.ASSIGN);
        addSimpleMatcher('-', TokenClass.MINUS);
        addSimpleMatcher('*', TokenClass.TIMES);
        addSimpleMatcher('%', TokenClass.MOD);
        addSimpleMatcher('/', TokenClass.DIV);

        addSimpleMatcher('<', TokenClass.LT);
        addSimpleMatcher('>', TokenClass.GT);

    }

    private void addSimpleMatcher(char identifier, TokenClass tokenClass) {
        matchers.add(new SimpleToken(identifier, scanner, tokenClass));
    }

    private void error(char c, int line, int col) {
        System.out.println("Lexing error: unrecognised character ("+c+") at "+line+":"+col);
	error++;
    }


    public Token nextToken() {
        Token result;
        try {
             result = next();
        } catch (EOFException eof) {
            // end of file, nothing to worry about, just return EOF token
            return new Token(TokenClass.EOF, scanner.getLine(), scanner.getColumn());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            // something went horribly wrong, abort
            System.exit(-1);
            return null;
        }
        return result;
    }

//    private Token next1() throws IOException {
//        char current = scanner.next();
//        char next = scanner.peek();
//
//        if (Character.isWhitespace(current)) {
//            return next1();
//        }
//
//        for (AbstractToken abstractToken : matchers) {
//            if (abstractToken.test(current, next)) {
//                return abstractToken.tokenize(current);
//            }
//
//        }
//
//        error(c, scanner.getLine(), scanner.getColumn());
//        return new Token(TokenClass.INVALID, scanner.getLine(), scanner.getColumn());
//    }

    /*
     * To be completed
     */
    private Token next() throws IOException {

        int line = scanner.getLine();
        int column = scanner.getColumn();

	    // get the next character
        char c = scanner.next();

        // skip white spaces
        if (Character.isWhitespace(c))
            return next();

        if (matchesPair(c, "==")) return new Token(TokenClass.EQ, line, column);
        if (matchesPair(c, "!=")) return new Token(TokenClass.NE, line, column);
        if (matchesPair(c, "<=")) return new Token(TokenClass.LE, line, column);
        if (matchesPair(c, ">=")) return new Token(TokenClass.GE, line, column);
        if (matchesPair(c, "//")) return comment(c);
        if (matchesPair(c, "/*")) return multilineComment(c);

        if (c == '+') return new Token(TokenClass.PLUS, line, column);
        if (c == ';') return new Token(TokenClass.SEMICOLON, line, column);
        if (c == ',') return new Token(TokenClass.COMMA, line, column);
        if (c == '(') return new Token(TokenClass.LPAR, line, column);
        if (c == ')') return new Token(TokenClass.RPAR, line, column);
        if (c == '{') return new Token(TokenClass.LBRA, line, column);
        if (c == '}') return new Token(TokenClass.RBRA, line, column);
        if (c == '=') return new Token(TokenClass.ASSIGN, line, column);
        if (c == '-') return new Token(TokenClass.MINUS, line, column);
        if (c == '*') return new Token(TokenClass.TIMES, line, column);
        if (c == '%') return new Token(TokenClass.MOD, line, column);
        if (c == '/') return new Token(TokenClass.DIV, line, column);

        if (c == '<') return new Token(TokenClass.LT, line, column);
        if (c == '>') return new Token(TokenClass.GT, line, column);

        if (c == '#') return service.include(c);

        if (Character.isDigit(c)) return new Token(TokenClass.NUMBER, line, column);
        if (c == '\'') return service.character();
        if (c == '"') return service.stringLiteral();

        String identifier = identifier(c);

        if (identifier.equals("int")) return new Token(TokenClass.INT, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("void")) return new Token(TokenClass.VOID, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("char")) return new Token(TokenClass.CHAR, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("if")) return new Token(TokenClass.IF, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("else")) return new Token(TokenClass.ELSE, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("while")) return new Token(TokenClass.WHILE, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("return")) return new Token(TokenClass.RETURN, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("print_s")) return new Token(TokenClass.PRINT, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("print_c")) return new Token(TokenClass.PRINT, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("print_i")) return new Token(TokenClass.PRINT, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("main")) return new Token(TokenClass.MAIN, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("read_c")) return new Token(TokenClass.READ, identifier, scanner.getLine(), scanner.getColumn());
        else if (identifier.equals("read_i")) return new Token(TokenClass.READ, identifier, scanner.getLine(), scanner.getColumn());
        else return new Token(TokenClass.IDENTIFIER, identifier, line, column);


//        if we reach this point, it means we did not recognise a valid token
//        error(c,line,column);
//        return new Token(TokenClass.INVALID, line, column);
    }

    private Token multilineComment(char c) throws IOException {

        StringBuilder buffer = new StringBuilder(Character.toString(c));
        // Consume astrix
        buffer.append(scanner.next());

        char last = scanner.next();
        // Start reading comment
        buffer.append(last);


        while (c != '*' && scanner.peek() != '/' && last != '\\') {
            c = scanner.next();
            buffer.append(c);

            if (c == '\\') { // We're escaping
                // Force read the next char to consume it
                c = scanner.next();
                buffer.append(c);
            }


            last = c;
        }

        // We've reached closing *, consume /
        buffer.append(scanner.next());

//        System.out.println("Multiline Comment: " + buffer);
        return next();
    }

    private Token comment(char c) throws IOException {
        // Go to the end of line
        char next = scanner.peek();
        while (next != '\n') {
            scanner.next();
            next = scanner.peek();
        }
        return next();
    }

    private boolean matchesPair(char current, String expected) throws IOException {
        try {
            return expected.equals(Character.toString(current) + scanner.peek());
        } catch (EOFException e) {
            return false;
        }

    }

    private String readUntilDelimiter() throws IOException {
        StringBuilder buffer = new StringBuilder();


        char next = scanner.peek();
        while (!delimiters.contains(next) && !Character.isWhitespace(next)) {
            buffer.append(scanner.next());
            next = scanner.peek();
        }
        return buffer.toString();
    }

    private String identifier(char c) throws IOException {
        StringBuilder buffer = new StringBuilder(Character.toString(c));

        try {
            char peek = scanner.peek();

            if (Character.isAlphabetic(peek))
                buffer.append(scanner.next());

            peek = scanner.peek();
            while (Character.isAlphabetic(peek) || Character.isDigit(peek) || peek == '_') {
                buffer.append(scanner.next());
                peek = scanner.peek();
            }

        } catch (EOFException e) {
            return buffer.toString();
        }
        return buffer.toString();
    }



}
