package lexer;

import lexer.exceptions.UnexpectedCharacter;

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
        StringBuffer buffer = new StringBuffer(startChar + "");

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

    private char readAndAssert(char expected) throws IOException, UnexpectedCharacter {
        char next = scanner.next();

        if (next != expected)
            throw new UnexpectedCharacter(next, scanner.getLine(), scanner.getColumn());

        return next;
    }

    public String readUntilWhitespace() throws IOException {
        StringBuffer buffer = new StringBuffer();


        while (!Character.isWhitespace(scanner.peek())) {
            char next = scanner.next();
            buffer.append(next);
        }

        return buffer.toString();
    }


}
