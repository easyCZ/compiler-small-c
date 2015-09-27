package lexer;

import lexer.Token.TokenClass;

import java.io.EOFException;
import java.io.IOException;

/**
 * @author cdubach
 */
public class Tokeniser {

    final String INCLUDE = "include";

    private Scanner scanner;
    private final TokeniserService service;

    private int error = 0;
    public int getErrorCount() {
	return this.error;
    }

    public Tokeniser(Scanner scanner) {
        this.scanner = scanner;
        this.service = new TokeniserService(scanner);
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

    /*
     * To be completed
     */
    private Token next() throws IOException {

        int line = scanner.getLine();
        int column = scanner.getColumn();

	    // get the next character
        char c = scanner.next();

        // skip white spaces
        if (Character.isWhitespace(c) || Character.isWhitespace(scanner.peek()))
            return next();

        if (matchesPair(c, "==")) return new Token(TokenClass.EQ, line, column);
        if (matchesPair(c, "!=")) return new Token(TokenClass.NE, line, column);
        if (matchesPair(c, "<=")) return new Token(TokenClass.LE, line, column);
        if (matchesPair(c, ">=")) return new Token(TokenClass.GE, line, column);

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
        if (c == '\'') return service.character(c);


        final String chunk = getNextChunk();
        System.out.println("Chunk: " + chunk);
	

        // if we reach this point, it means we did not recognise a valid token
        error(c,line,column);
        return new Token(TokenClass.INVALID, line, column);
    }


    private String getNextChunk() throws IOException {
        StringBuilder buffer = new StringBuilder();
        while (!Character.isWhitespace(scanner.peek())) {
            buffer.append(scanner.next());
        }
        return buffer.toString();
    }

    private boolean matchesPair(char current, String expected) throws IOException {
        return expected.equals(current + scanner.peek());
    }

}
