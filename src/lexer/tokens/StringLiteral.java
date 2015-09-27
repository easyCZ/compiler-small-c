package lexer.tokens;


import lexer.Scanner;
import lexer.Token;

import java.io.EOFException;
import java.io.IOException;

public class StringLiteral extends AbstractToken {

    private final Scanner scanner;

    public StringLiteral(char identifier, Scanner scanner) {
        super(identifier);
        this.scanner = scanner;
    }

    @Override
    public Token tokenize(char c) throws IOException {
        StringBuilder buffer = new StringBuilder();

        try {
            while (scanner.peek() != '"' || isEscaped(c)) {
                // abc"
                c = scanner.next();
                buffer.append(c);
            }

            // Consume quote
            scanner.next();

        } catch (EOFException e) {
            return new lexer.Token(lexer.Token.TokenClass.INVALID, buffer.toString(), scanner.getLine(), scanner.getColumn());
        }

        return new lexer.Token(lexer.Token.TokenClass.STRING_LITERAL, buffer.toString(), scanner.getLine(), scanner.getColumn());
    }
}
