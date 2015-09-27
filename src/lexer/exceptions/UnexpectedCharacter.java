package lexer.exceptions;


public class UnexpectedCharacter extends Exception {

    public final char character;
    public final int line;
    public final int col;

    public UnexpectedCharacter(char c, int line, int col) {
        this.character = c;
        this.line = line;
        this.col = col;
    }
}
