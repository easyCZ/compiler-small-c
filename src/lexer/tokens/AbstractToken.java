package lexer.tokens;

import lexer.Token;

import java.io.IOException;

public abstract class AbstractToken<A> {

    public final A identifier;

    public AbstractToken(A identifier) {
        this.identifier = identifier;
    }

    public boolean test(A c) {
        return this.identifier.equals(c);
    }

    public abstract Token tokenize(char c) throws IOException;

    public boolean isEscaped(char previous) {
        return previous == '\\';
    }

}
