package lexer;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TokeniserServiceTest {

    private TokeniserService sut;

    private static final char IDENTIFIER = '#';
    private static final String INCLUDE = "include";
    private static final String INVALID_INCLUDE = "incABCDEF ";

    @Test
    public void test_include_reads_all() throws IOException {
        sut = getSut(INCLUDE);

        Token token = sut.include(IDENTIFIER);
        assertEquals(token.tokenClass, Token.TokenClass.INCLUDE);
        assertEquals(token.data, IDENTIFIER + INCLUDE);
        assertEquals(token.position.toString(), "1:8");
    }

    @Test
    public void test_invalid_marks_token_invalid() throws IOException {
        sut = getSut(INVALID_INCLUDE);
        Token token = sut.include('#');
        assertEquals(token.data, '#' + INVALID_INCLUDE.trim());
    }

    @Test
    public void test_invalid_sets_position_pointer_to_first_invalid_char() throws IOException {
        Scanner scanner = buildScanner(INVALID_INCLUDE);
        sut = getSut(scanner);

        Token token = sut.include('#');
        assertEquals(token.position.toString(), "1:5");

        // Should be at the end of the chunk
        assertEquals(scanner.getLine(), 1);
        assertEquals(scanner.getColumn(), 10);
    }

    private Scanner buildScanner(String content) {
        return new Scanner(content);
    }

    private TokeniserService getSut(String content) {
        return new TokeniserService(buildScanner(content));
    }

    private TokeniserService getSut(Scanner scanner) {
        return new TokeniserService(scanner);
    }
}