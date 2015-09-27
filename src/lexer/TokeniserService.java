package lexer;

import lexer.exceptions.UnexpectedCharacter;

import java.io.EOFException;
import java.io.IOException;

public class TokeniserService {

    private final Scanner scanner;

    public TokeniserService(Scanner scanner) {
        this.scanner = scanner;
    }

    /*
        We have detected that we have # symbol, read the rest and validate.
     */
    public Token include(char startChar) throws IOException {
        StringBuilder buffer = new StringBuilder(Character.toString(startChar));

        try {
            buffer.append(readAndAssert('i'));
            buffer.append(readAndAssert('n'));
            buffer.append(readAndAssert('c'));
            buffer.append(readAndAssert('l'));
            buffer.append(readAndAssert('u'));
            buffer.append(readAndAssert('d'));
            buffer.append(readAndAssert('e'));
        }
        catch (UnexpectedCharacter e) {

            buffer.append(e.character);

            int line = scanner.getLine();
            int col = scanner.getColumn();

            // Read until the next whitespace, mark everything as unexpected
            buffer.append(readUntilWhitespace());
            return new Token(Token.TokenClass.INVALID, buffer.toString(), line, col);
        }

        return new Token(Token.TokenClass.INCLUDE, buffer.toString(), scanner.getLine(), scanner.getColumn());
    }

    public Token stringLiteral() throws IOException {
        StringBuilder buffer = new StringBuilder();

        char c;
        try {
            c = scanner.peek();
            while (scanner.peek() != '"' || isEscaped(c)) {
                c = scanner.next();
                buffer.append(c);
            }
        } catch (EOFException e) {
            return new Token(Token.TokenClass.INVALID, buffer.toString(), scanner.getLine(), scanner.getColumn());
        }

        return new Token(Token.TokenClass.STRING_LITERAL, buffer.toString(), scanner.getLine(), scanner.getColumn());
    }

    public String readUntilWhitespace() throws IOException {
        StringBuilder buffer = new StringBuilder();


        while (!nextIsWhitespace()) {
            char next = scanner.next();
            buffer.append(next);
        }

        return buffer.toString();
    }

    public Token character() throws IOException {
        StringBuilder buffer = new StringBuilder();

        // Read until we reach a closing quote
        // Escaped quotes should be skipped
        char c;
        try {
            c = scanner.peek();

            while (scanner.peek() != '\'' || isEscaped(c /* previous */)) {
                c = scanner.next();
                buffer.append(c);
            }
        } catch (EOFException e) {
            return new Token(Token.TokenClass.INVALID, buffer.toString(), scanner.getLine(), scanner.getColumn());
        }

        return new Token(Token.TokenClass.CHARACTER, buffer.toString(), scanner.getLine(), scanner.getColumn());
    }

    private boolean nextIsWhitespace() throws IOException {
        return Character.isWhitespace(scanner.peek());
    }

    private boolean isEscaped(char previous) {
        return previous == '\\';
    }

    private char readAndAssert(char expected) throws IOException, UnexpectedCharacter {
        char next = scanner.next();

        if (next != expected)
            throw new UnexpectedCharacter(next, scanner.getLine(), scanner.getColumn());

        return next;
    }

}
