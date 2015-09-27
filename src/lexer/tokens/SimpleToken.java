package lexer.tokens;


import lexer.Scanner;
import lexer.Token;

import java.io.IOException;

public class SimpleToken extends AbstractToken<Character> {

    private final Token.TokenClass matchClass;
    private final Scanner scanner;

    public SimpleToken(char identifier, Scanner scanner, Token.TokenClass matchClass) {
        super(identifier);
        this.matchClass = matchClass;
        this.scanner = scanner;
    }

    @Override
    public Token tokenize(char c) throws IOException {
        return new Token(
                matchClass,
                Character.toString(c),
                scanner.getLine(),
                scanner.getColumn());
    }

}
